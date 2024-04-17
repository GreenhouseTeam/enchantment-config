package dev.greenhouseteam.enchantmentconfig.api.registries;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableCodecFunction;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistries {
    public static final Registry<MapCodec<? extends Condition>> CONDITION_CODEC = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.CONDITION_CODEC_KEY);
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY);
    public static final Registry<VariableType<?>> VARIABLE_TYPE = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.VARIABLE_TYPE);
    public static final Registry<VariableCodecFunction> VARIABLE_CODEC = EnchantmentConfigUtil.getHelper().createRegistry(EnchantmentConfigRegistryKeys.VARIABLE_CODEC_KEY);
}
