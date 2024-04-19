package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.resources.ResourceLocation;

public abstract class VariableSerializer<I, O> {
    public abstract VariableType<I> inputType(VariableType<?> inputType);

    public abstract VariableType<O> outputType(VariableType<?> inputType);

    public abstract MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType);

    public abstract ResourceLocation id();
}
