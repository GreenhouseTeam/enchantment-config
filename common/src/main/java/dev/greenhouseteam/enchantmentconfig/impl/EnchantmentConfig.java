package dev.greenhouseteam.enchantmentconfig.impl;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.BooleanVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.DoubleVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.FloatVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.IntVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.LongVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;

public class EnchantmentConfig {
    private static ModificationType currentModificationType;

    public static void setModificationType(ModificationType type) {
        currentModificationType = type;
    }

    public static ModificationType getAndClearModificationType() {
        ModificationType retValue = currentModificationType;
        currentModificationType = null;
        return retValue;
    }

    public static void registerConditionCodecs(RegistrationCallback<MapCodec<? extends Condition>> callback) {
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, Condition.And.ID, Condition.And.CODEC);
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, Condition.Or.ID, Condition.Or.CODEC);
        callback.register(EnchantmentConfigRegistries.CONDITION_CODEC, Condition.Variable.ID, Condition.Variable.CODEC);
    }

    public static void registerVariableTypes(RegistrationCallback<VariableType<?>> callback) {
        callback.register(EnchantmentConfigRegistries.VARIABLE_TYPE, BooleanVariableType.ID, VariableTypes.BOOLEAN);
        callback.register(EnchantmentConfigRegistries.VARIABLE_TYPE, DoubleVariableType.ID, VariableTypes.DOUBLE);
        callback.register(EnchantmentConfigRegistries.VARIABLE_TYPE, FloatVariableType.ID, VariableTypes.FLOAT);
        callback.register(EnchantmentConfigRegistries.VARIABLE_TYPE, IntVariableType.ID, VariableTypes.INT);
        callback.register(EnchantmentConfigRegistries.VARIABLE_TYPE, LongVariableType.ID, VariableTypes.LONG);
    }
}