package dev.greenhouseteam.enchantmentconfig.api.registries;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.EnchantmentCondition;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class EnchantmentConfigRegistryKeys {
    public static final ResourceKey<Registry<MapCodec<? extends EnchantmentCondition>>> CONDITION_CODEC_KEY = ResourceKey.createRegistryKey(EnchantmentConfigUtil.asResource("condition_codec"));
    public static final ResourceKey<Registry<EnchantmentType<?>>> ENCHANTMENT_TYPE_KEY = ResourceKey.createRegistryKey(EnchantmentConfigUtil.asResource("enchantment_type"));
    public static final ResourceKey<Registry<MapCodec<? extends EnchantmentVariable<?>>>> ENCHANTMENT_VARIABLE_CODEC_KEY = ResourceKey.createRegistryKey(EnchantmentConfigUtil.asResource("enchantment_type"));
}
