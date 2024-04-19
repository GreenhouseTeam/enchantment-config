
package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class FieldCodec<I, O> implements Codec<Field<I, O>> {
    @Nullable
    private VariableType<I> inputType;
    @Nullable
    private VariableType<O> outputType;

    protected FieldCodec(@Nullable VariableType<I> inputType, @Nullable VariableType<O> outputType) {
        this.inputType = inputType;
        this.outputType = outputType;
    }

    @Override
    public <T> DataResult<Pair<Field<I, O>, T>> decode(DynamicOps<T> ops, T input) {
        var singularValueInput = input;
        var dataResult = ops.getMap(input);
        if (dataResult.isSuccess()) {
            if (inputType == null) {
                var typeT = dataResult.result().get().get("type");
                var serializer = EnchantmentConfigRegistries.VARIABLE_SERIALIZER.byNameCodec().decode(ops, typeT);
                if (serializer.isSuccess()) {
                    inputType = (VariableType<I>) serializer.result().get().getFirst().inputType(null);
                    outputType = (VariableType<O>) serializer.result().get().getFirst().outputType(null);
                }
            }
            if (inputType != null) {
                if (outputType == null)
                    outputType = (VariableType<O>) inputType;
                var variable = Variable.dispatchCodec(inputType).decode(ops, input);
                if (variable.isSuccess())
                    return (DataResult<Pair<Field<I, O>, T>>) variable.result().map(pair -> {
                        if (outputType != null && pair.getFirst().getSerializer().outputType(inputType) != outputType)
                            return DataResult.error(() -> "Could not cast variable '" + pair.getFirst().getSerializer().id() + "' to '" + EnchantmentConfigRegistries.VARIABLE_TYPE.getKey(outputType) + "'. Expected " + EnchantmentConfigRegistries.VARIABLE_TYPE.getKey(pair.getFirst().getSerializer().outputType(inputType)) + ".");
                        return DataResult.success(Pair.of(new Field(pair.getFirst(), inputType), pair.getSecond()));
                    }).or(() -> DataResult.error(() -> "Failed to decode VariableFieldCodec for unknown reasons.").error()).orElseThrow();
                return DataResult.error(() -> "Could not deserialize variable: " + variable.error().get());
            } else {
                var typeT = dataResult.result().get().get("variable_type");
                var serializer = EnchantmentConfigRegistries.VARIABLE_TYPE.byNameCodec().decode(ops, typeT);
                if (serializer.isSuccess()) {
                    inputType = (VariableType<I>) serializer.result().get().getFirst();
                    singularValueInput = dataResult.result().get().get("value");
                } else
                    return DataResult.error(() -> "Could not deserialize 'variable_type' field." + serializer.error().get().message());
            }
        }
        if (outputType == null)
            outputType = (VariableType<O>) inputType;
        var elementCodec = outputType.getValueCodec();
        var valueResult = elementCodec.decode(ops, singularValueInput).map(pair -> DataResult.success(Pair.of(new Field<>((O) pair.getFirst(), outputType), pair.getSecond())));
        if (valueResult.isSuccess())
            return (DataResult<Pair<Field<I, O>,T>>)(Object)valueResult.result().get();
        else
            return DataResult.error(() -> "Could not decode static value: " + valueResult.error().get().message());
    }

    @Override
    public <T> DataResult<T> encode(Field<I, O> input, DynamicOps<T> ops, T prefix) {
        if (input.getInnerVariable() != null)
            return Variable.dispatchCodec(input.getInputType()).encode(input.getInnerVariable(), ops, prefix);
        return input.getOutputType().getValueCodec().encode(input.getRawValue(), ops, prefix);
    }

    @Override
    public String toString() {
        return "VariableFieldCodec[" + outputType + ']';
    }

}