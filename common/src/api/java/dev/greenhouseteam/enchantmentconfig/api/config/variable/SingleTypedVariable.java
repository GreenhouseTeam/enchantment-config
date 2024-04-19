package dev.greenhouseteam.enchantmentconfig.api.config.variable;

public interface SingleTypedVariable<T> extends Variable<T, T> {
    VariableSerializer<T, T> getSerializer();
}
