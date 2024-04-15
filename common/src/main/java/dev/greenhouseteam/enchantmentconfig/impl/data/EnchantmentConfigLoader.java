package dev.greenhouseteam.enchantmentconfig.impl.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigGetterImpl;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.resources.RegistryOps;
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

    protected EnchantmentConfigLoader() {
    }

    @Override
    protected Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager manager, ProfilerFiller filler) {
        Map<ResourceLocation, List<JsonElement>> map = new HashMap<>();

        for(Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks("configurations", key -> {
            Optional<EnchantmentType<?>> enchantmentType = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.getOptional(configResource(key));
            return key.getNamespace().equals(EnchantmentConfigUtil.MOD_ID) && enchantmentType.isPresent() && key.getPath().endsWith(".json");
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

    private ResourceLocation configResource(ResourceLocation key) {
        String namespace = key.getPath().split("/")[1];
        String path = key.getPath().split("/")[2];
        return new ResourceLocation(namespace, path.substring(0, path.length() - 5));
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> map, ResourceManager manager, ProfilerFiller filler) {
        DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, EnchantmentConfigUtil.getHelper().getRegistries());
        ResourceLocation globalKey = EnchantmentConfigUtil.asResource("global");
        ConfiguredEnchantment<?, ?> globalConfigured = handleJson(globalKey, ops, map.getOrDefault(globalKey, List.of()), Optional.empty());
        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : map.entrySet().stream().filter(entry -> !entry.getKey().equals(globalKey)).toList()) {
            handleJson(entry.getKey(), ops, entry.getValue(), Optional.ofNullable(globalConfigured));
        }
    }

    private ConfiguredEnchantment<?, ?> handleJson(ResourceLocation key, DynamicOps<JsonElement> ops, List<JsonElement> elements, Optional<ConfiguredEnchantment<?, ?>> global) {
        ConfiguredEnchantment<?, ?> currentConfigured = null;
        for (JsonElement json : elements) {
            ConfiguredEnchantment<?, ?> configured = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.get(key).codec().decode(ops, json).getOrThrow().getFirst();
            if (currentConfigured != null) {
                configured = configured.merge(currentConfigured, global);
            }
            currentConfigured = configured;
        }
        if (currentConfigured != null)
            ((EnchantmentConfigGetterImpl)EnchantmentConfigGetter.INSTANCE).register(currentConfigured);
        return currentConfigured;
    }
}
