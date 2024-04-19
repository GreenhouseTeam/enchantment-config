package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;

public abstract class SingleTypedSerializer<T> extends VariableSerializer<T, T> {
    public abstract VariableType<T> type(VariableType<?> inputType);

    @Override
    public VariableType<T> inputType(VariableType<?> inputType) {
        return type(inputType);
    }

    @Override
    public VariableType<T> outputType(VariableType<?> inputType) {
        return type(inputType);
    }
}
