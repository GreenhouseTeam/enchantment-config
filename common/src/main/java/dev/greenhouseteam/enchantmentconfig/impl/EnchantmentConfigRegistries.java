package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistries {
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE_REGISTRY = EnchantmentConfig.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY);
}
