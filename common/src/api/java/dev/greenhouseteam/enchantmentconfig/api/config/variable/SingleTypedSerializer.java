package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import org.jetbrains.annotations.Nullable;

public abstract class SingleTypedSerializer<T> extends VariableSerializer<T, T> {
    /**
     * The {@link VariableType} of this variable serializer, used for both the input and output.
     *
     * @param contextType The context's variable type.
     * @return          The variable type used for serialization.
     */
    public abstract VariableType<T> type(@Nullable VariableType<?> contextType);

    @Override
    public VariableType<T> inputType(@Nullable VariableType<?> contextType) {
        return type(contextType);
    }

    @Override
    public VariableType<T> outputType(@Nullable VariableType<?> contextType) {
        return type(contextType);
    }
}
