package dev.greenhouseteam.enchantmentconfig.api;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

// TODO: Document this.
public interface EnchantmentConfigAssigner {
    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType);

    default <T> void registerVariableCodec(ResourceLocation id, MapCodec<? extends Variable<T>> codec) {
        registerVariableCodec(id, null, type -> (MapCodec<? extends Variable<Object>>) codec);
    }

    default <T> void registerVariableCodec(ResourceLocation id, Function<VariableType<T>, MapCodec<? extends Variable<T>>> variableTypeToCodecFunction) {
        registerVariableCodec(id, null, variableTypeToCodecFunction);
    }

    default <T> void registerVariableCodec(ResourceLocation id, VariableType<?> variableType, MapCodec<? extends Variable<T>> codec) {
        registerVariableCodec(id, variableType, type -> (MapCodec<? extends Variable<Object>>) codec);
    }

    <T> void registerVariableCodec(ResourceLocation id, VariableType<?> variableType, Function<VariableType<T>, MapCodec<? extends Variable<T>>> variableTypeToCodecFunction);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, ExtraFieldType<?> extraFieldType);
}
