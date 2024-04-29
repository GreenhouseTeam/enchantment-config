package dev.greenhouseteam.enchantmentconfig.api.registries;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistries {
    public static final Registry<MapCodec<? extends Condition>> CONDITION_CODEC = EnchantmentConfigApi.getHelper().createRegistry(EnchantmentConfigRegistryKeys.CONDITION_CODEC_KEY);
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE = EnchantmentConfigApi.getHelper().createRegistry(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY);
    public static final Registry<VariableType<?>> VARIABLE_TYPE = EnchantmentConfigApi.getHelper().createRegistry(EnchantmentConfigRegistryKeys.VARIABLE_TYPE);
    public static final Registry<VariableSerializer<?, ?>> VARIABLE_SERIALIZER = EnchantmentConfigApi.getHelper().createRegistry(EnchantmentConfigRegistryKeys.VARIABLE_SERIALIZER_KEY);
}
