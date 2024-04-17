package dev.greenhouseteam.enchantmentconfig.impl.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigGetterImpl;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.EnchantmentCondition;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

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

    protected EnchantmentConfigLoader() {
    }

    @Override
    protected Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager manager, ProfilerFiller filler) {
        Map<ResourceLocation, List<JsonElement>> map = new HashMap<>();

        for(Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks("enchantmentconfig/configurations", key -> {
            if (!key.getPath().endsWith(".json"))
                return false;
            if (isGlobal(configResource(key)))
                return true;
            Optional<EnchantmentType<?>> enchantmentType = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.getOptional(configResource(key));
            return enchantmentType.isPresent();
        }).entrySet()) {
            ResourceLocation key = entry.getKey();
            ResourceLocation fileToId = configResource(key);

            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    map.computeIfAbsent(fileToId, rl -> new ArrayList<>()).add(element);
                } catch (IllegalArgumentException | IOException | JsonParseException var14) {
                    EnchantmentConfigUtil.LOGGER.error("Couldn't parse data file {} from {}", fileToId, key, var14);
                }
            }
        }

        return map;
    }

    private boolean isGlobal(ResourceLocation key) {
        return key.getNamespace().equals(EnchantmentConfigUtil.MOD_ID) && key.getPath().startsWith("global/");
    }

    private ResourceLocation configResource(ResourceLocation key) {
        String namespace = key.getPath().split("/", 4)[2];
        String path = key.getPath().split("/", 4)[3];
        return new ResourceLocation(namespace, path.substring(0, path.length() - 5));
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> map, ResourceManager manager, ProfilerFiller filler) {
        ((EnchantmentConfigGetterImpl) EnchantmentConfigGetter.INSTANCE).clear();

        DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, EnchantmentConfigUtil.getHelper().getRegistryLookup());
        Map<ResourceLocation, ConfiguredEnchantment<?, ?>> globalConfigured = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : map.entrySet().stream().filter(entry -> isGlobal(entry.getKey())).toList()) {
            for (Map.Entry<ResourceKey<EnchantmentType<?>>, EnchantmentType<?>> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE.entrySet()) {
                var configured = handleJson(type.getKey().location(), ops, entry.getValue(), Optional.empty(), true);
                if (configured != null)
                    globalConfigured.put(type.getKey().location(), configured);
            }
            hasLoggedError = false;
        }
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : map.entrySet().stream().filter(entry -> !isGlobal(entry.getKey())).toList()) {
            handleJson(entry.getKey(), ops, entry.getValue(), Optional.ofNullable(globalConfigured.getOrDefault(entry.getKey(), null)), false);
            hasLoggedError = false;
        }
    }

    private ConfiguredEnchantment<?, ?> handleJson(ResourceLocation key, DynamicOps<JsonElement> ops, List<JsonElement> elements, Optional<ConfiguredEnchantment<?, ?>> global, boolean globalContext) {
        Optional<ConfiguredEnchantment<?, ?>> currentConfigured = Optional.empty();
        if (elements.isEmpty() && global.isPresent())
            elements.add(new JsonObject());
        for (JsonElement json : elements) {
            ConfiguredEnchantment<?, ?> configured = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.get(key).codec().decode(ops, json).getOrThrow().getFirst();
            if (((JsonObject)json).has("condition")) {
                EnchantmentCondition condition = EnchantmentCondition.CODEC.decode(ops, ((JsonObject)json).get("condition")).getOrThrow().getFirst();
                try {
                    if (!condition.compare(configured.getType()))
                        continue;
                } catch (UnsupportedOperationException ex) {
                    if (globalContext) {
                        if (!hasLoggedError)
                            EnchantmentConfigUtil.LOGGER.error("Failed to decode enchantment configuration '" + key + "'. Failed to compare variable based condition: " + ex.getMessage());
                        hasLoggedError = true;
                    }
                    continue;
                }
            }
            if (currentConfigured.isPresent() || global.isPresent())
                configured = configured.merge(currentConfigured, global);

            currentConfigured = Optional.of(configured);
        }
        currentConfigured.ifPresent(configuredEnchantment -> ((EnchantmentConfigGetterImpl) EnchantmentConfigGetter.INSTANCE).register(configuredEnchantment));
        return currentConfigured.orElse(null);
    }
}
