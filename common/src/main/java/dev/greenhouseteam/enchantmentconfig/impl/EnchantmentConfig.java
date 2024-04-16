package dev.greenhouseteam.enchantmentconfig.impl;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.EnchantmentCondition;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;

public class EnchantmentConfig {
    public static void registerConditionCodecs(RegistrationCallback<MapCodec<? extends EnchantmentCondition>> callback) {
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, EnchantmentCondition.And.ID, EnchantmentCondition.And.CODEC);
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, EnchantmentCondition.Or.ID, EnchantmentCondition.Or.CODEC);
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, EnchantmentCondition.Variable.ID, EnchantmentCondition.Variable.CODEC);
    }
}