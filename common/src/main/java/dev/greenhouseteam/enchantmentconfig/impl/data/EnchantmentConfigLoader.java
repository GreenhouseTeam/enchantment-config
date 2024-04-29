package dev.greenhouseteam.enchantmentconfig.impl.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigGetterImpl;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantmentConfigLoader extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonElement>>> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean hasLoggedError = false;
    private static Map<RegistryAccess.RegistryEntry<?>, EnchantmentConfigTagLookup<?>> lookups = new HashMap<>();

    public EnchantmentConfigLoader() {
    }

    @Override
    protected Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager manager, ProfilerFiller filler) {
        Map<ResourceLocation, List<JsonElement>> jsonMap = new HashMap<>();

        for(Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks("enchantmentconfig/global_configs", key ->
                key.getPath().endsWith(".json")).entrySet()) {
            prepareFile(entry, jsonMap, true);
        }

        for(Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks("enchantmentconfig/configs", key -> {
            if (!key.getPath().endsWith(".json"))
                return false;
            Optional<EnchantmentType<?>> enchantmentType = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.getOptional(configResource(key, false));
            return enchantmentType.isPresent();
        }).entrySet()) {
            prepareFile(entry, jsonMap, false);
        }

        return jsonMap;
    }

    private void prepareFile(Map.Entry<ResourceLocation, List<Resource>> entry, Map<ResourceLocation, List<JsonElement>> jsonMap, boolean global) {
        ResourceLocation key = entry.getKey();
        ResourceLocation fileToId = configResource(key, global);

        for (Resource resource : entry.getValue()) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                jsonMap.computeIfAbsent(fileToId, rl -> new ArrayList<>()).add(element);
            } catch (IllegalArgumentException | IOException | JsonParseException var14) {
                EnchantmentConfigApi.LOGGER.error("Couldn't parse data file {} from {}", fileToId, key, var14);
            }
        }
    }

    private boolean isGlobal(ResourceLocation key) {
        // Use / here as it cannot be a file name starter on both Windows and Unix systems.
        return key.getPath().startsWith("/global_configs/");
    }

    private ResourceLocation configResource(ResourceLocation key, boolean global) {
        String namespace = global ? key.getNamespace() : key.getPath().split("/", 4)[2];
        String path = global ? key.getPath().split("/", 3)[2] : key.getPath().split("/", 4)[3];
        return new ResourceLocation(namespace, (global ? "/global_configs/" : "") + path.substring(0, path.length() - 5));
    }



    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> map, ResourceManager manager, ProfilerFiller filler) {
        ((EnchantmentConfigGetterImpl) EnchantmentConfigGetter.INSTANCE).clear();

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, createContext(EnchantmentConfig.getRegistryLookup()));
        Map<ResourceLocation, ConfiguredEnchantment<?, ?>> globalConfigured = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : map.entrySet().stream().filter(entry -> isGlobal(entry.getKey())).toList()) {
            for (Map.Entry<ResourceKey<EnchantmentType<?>>, EnchantmentType<?>> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE.entrySet()) {
                var configured = handleJson(type.getKey().location(), ops, entry.getValue(), Optional.empty(), entry.getKey());
                if (configured != null)
                    globalConfigured.put(type.getKey().location(), configured);
            }
            hasLoggedError = false;
        }
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : map.entrySet().stream().filter(entry -> !isGlobal(entry.getKey())).toList()) {
            handleJson(entry.getKey(), ops, entry.getValue(),
                    Optional.ofNullable(globalConfigured.getOrDefault(entry.getKey(), null)),
                    null);
            hasLoggedError = false;
        }
        EnchantmentConfig.setRegistryLookup(null);
        EnchantmentConfig.setTags(null);
        lookups.values().forEach(EnchantmentConfigTagLookup::resetHolders);
    }

    private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess access) {
        final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap<>();
        access.registries().forEach(registry -> {
            if (!lookups.containsKey(registry))
                lookups.put(registry, new EnchantmentConfigTagLookup<>(registry.value()));;
            map.put(registry.key(), new RegistryOps.RegistryInfo(registry.value().asLookup(), lookups.get(registry), registry.value().registryLifecycle()));
        });
        return new RegistryOps.RegistryInfoLookup() {
            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
                return Optional.ofNullable((RegistryOps.RegistryInfo<T>)map.get(key));
            }
        };
    }

    private ConfiguredEnchantment<?, ?> handleJson(ResourceLocation key, DynamicOps<JsonElement> ops, List<JsonElement> elements, Optional<ConfiguredEnchantment<?, ?>> global, @Nullable ResourceLocation fileKey) {
        if (fileKey == null || !hasLoggedError) {
            Optional<ConfiguredEnchantment<?, ?>> currentConfigured = Optional.empty();
            if (elements.isEmpty() && global.isPresent())
                elements.add(new JsonObject());
            for (JsonElement json : elements) {
                try {
                    ConfiguredEnchantment<?, ?> configured = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.get(key).codec().decode(ops, json).getOrThrow().getFirst();
                    Optional<Condition> condition = Condition.CODEC.optionalFieldOf("condition").decode(ops, ops.getMap(json).getOrThrow()).getOrThrow(s -> new IllegalStateException("Failed to decode enchantment condition: " + s));
                    if (condition.isPresent()) {
                        try {
                            if (!condition.get().compare(configured.getType()))
                                continue;
                        } catch (UnsupportedOperationException ex) {
                            throw new IllegalStateException("Failed to compare variable based condition: " + ex.getMessage());
                        }
                    }

                    if (currentConfigured.isPresent() || global.isPresent())
                        configured = configured.merge(currentConfigured, global);

                    currentConfigured = Optional.of(configured);
                } catch (Exception ex) {
                    EnchantmentConfigApi.LOGGER.error("Failed to decode enchantment configuration '{}'.", (fileKey != null ? fileKey : key), ex);
                    hasLoggedError = true;
                }
            }
            currentConfigured.ifPresent(configuredEnchantment -> ((EnchantmentConfigGetterImpl) EnchantmentConfigGetter.INSTANCE).register(configuredEnchantment));
            return currentConfigured.orElse(null);
        }
        return null;
    }
}
