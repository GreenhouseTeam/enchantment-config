package dev.greenhouseteam.enchantmentconfig.api.registries;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistries {
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY);
    public static final Registry<MapCodec<? extends EnchantmentVariable<?>>> ENCHANTMENT_VARIABLE_CODEC = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_VARIABLE_CODEC_KEY);
}
