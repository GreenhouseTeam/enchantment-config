package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.platform.services.IEnchantmentConfigPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class EnchantmentConfigRegistries {
    public static final ResourceKey<Registry<EnchantmentType<?>>> ENCHANTMENT_TYPE_KEY = ResourceKey.createRegistryKey(EnchantmentConfigUtil.asResource("enchantment_type"));
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE_REGISTRY = IEnchantmentConfigPlatformHelper.INSTANCE.getEnchantmentTypeRegistry();
}
