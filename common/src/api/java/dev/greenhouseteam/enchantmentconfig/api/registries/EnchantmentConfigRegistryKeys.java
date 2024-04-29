package dev.greenhouseteam.enchantmentconfig.api.registries;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class EnchantmentConfigRegistryKeys {
    public static final ResourceKey<Registry<MapCodec<? extends Condition>>> CONDITION_CODEC_KEY = ResourceKey.createRegistryKey(EnchantmentConfigApi.asResource("condition_codec"));
    public static final ResourceKey<Registry<EnchantmentType<?>>> ENCHANTMENT_TYPE_KEY = ResourceKey.createRegistryKey(EnchantmentConfigApi.asResource("enchantment_type"));
    public static final ResourceKey<Registry<VariableSerializer<?, ?>>> VARIABLE_SERIALIZER_KEY = ResourceKey.createRegistryKey(EnchantmentConfigApi.asResource("variable_serializer"));
    public static final ResourceKey<Registry<VariableType<?>>> VARIABLE_TYPE = ResourceKey.createRegistryKey(EnchantmentConfigApi.asResource("variable_type"));
}
