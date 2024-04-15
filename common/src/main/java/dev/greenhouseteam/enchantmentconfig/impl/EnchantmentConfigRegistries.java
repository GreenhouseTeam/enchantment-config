package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistries {
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY);
}
