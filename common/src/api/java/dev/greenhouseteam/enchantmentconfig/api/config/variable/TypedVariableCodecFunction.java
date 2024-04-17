package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TypedVariableCodecFunction {
    private final VariableType<?> type;
    private final VariableCodecFunction function;

    public TypedVariableCodecFunction(VariableType<?> type, VariableCodecFunction function) {
        this.type = type;
        this.function = function;
    }

    public VariableType<?> getType() {
        return type;
    }

    public VariableCodecFunction getFunction() {
        return function;
    }

}
