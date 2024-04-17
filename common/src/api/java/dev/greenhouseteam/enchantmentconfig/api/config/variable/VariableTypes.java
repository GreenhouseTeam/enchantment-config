package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.BooleanVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.DoubleVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.FloatVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.IntVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.LongVariableType;

public class VariableTypes {
    public static final BooleanVariableType BOOLEAN = new BooleanVariableType();
    public static final DoubleVariableType DOUBLE = new DoubleVariableType();
    public static final IntVariableType INT = new IntVariableType();
    public static final FloatVariableType FLOAT = new FloatVariableType();
    public static final LongVariableType LONG = new LongVariableType();
}
