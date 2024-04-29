package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class VariableSerializer<I, O> {
    /**
     * The {@link VariableType} of this variable serializer, required as an input.
     *
     * @param contextType   The context's variable type.
     * @return              The variable type used as an input for serialization.
     */
    public abstract VariableType<I> inputType(@Nullable VariableType<?> contextType);

    /**
     * The {@link VariableType} of this variable serializer to be output as.
     *
     * @param contextType   The context's variable type.
     * @return              The variable type used as the output within serialization.
     */
    public abstract VariableType<O> outputType(@Nullable VariableType<?> contextType);

    /**
     * The codec for the Variable associated with this serializer.
     * It's recommended to put the serializer
     *
     * @param variableType The context {@link VariableType}.
     * @return A MapCodec used for serializing the associated variable.
     *
     * @see Variable
     */
    public abstract MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType);

    /**
     *
     *
     * @return The {@link ResourceLocation} associated with this serializer.
     *         Used within registration, and the <code>"type"</code> field of any variables.
     */
    public abstract ResourceLocation id();
}
