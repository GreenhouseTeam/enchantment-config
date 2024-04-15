package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.impl.util.RegistrationCallback;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentConfigAssignerImpl implements EnchantmentConfigAssigner {
    private static final Map<ResourceKey<EnchantmentType<?>>, EnchantmentType<?>> ENCHANTMENT_TYPE_MAP = new HashMap<>();

    public <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> T registerEnchantmentType(T enchantmentType) {
        ENCHANTMENT_TYPE_MAP.put(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, enchantmentType.getPath()), enchantmentType);
        return enchantmentType;
    }

    @Override
    public void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType) {
        if (!EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY.containsKey(enchantmentType)) {
            throw new NullPointerException("Tried adding field to EnchantmentType '" + enchantmentType.location() + "', which could not be found in the enchantment type registry.");
        }
        ENCHANTMENT_TYPE_MAP.get(enchantmentType).addExtraFieldType(path, extraFieldType);
    }

    public static void registerTypes(RegistrationCallback<EnchantmentType<?>> callback) {
        ENCHANTMENT_TYPE_MAP.forEach((key, type) -> callback.register(EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY, key.location(), type));
    }
}
