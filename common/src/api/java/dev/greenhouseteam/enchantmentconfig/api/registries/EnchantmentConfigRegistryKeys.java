package dev.greenhouseteam.enchantmentconfig.api.registries;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class EnchantmentConfigRegistryKeys {
    public static final ResourceKey<Registry<EnchantmentType<?>>> ENCHANTMENT_TYPE_KEY = ResourceKey.createRegistryKey(EnchantmentConfigUtil.asResource("enchantment_type"));
}
