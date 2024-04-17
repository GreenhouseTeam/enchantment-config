
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

@ApiStatus.Internal
public class FieldCodec<A> implements Codec<Field<A>> {
    private final Codec<A> elementCodec;
    private final VariableType<A> type;

    protected FieldCodec(final Codec<A> elementCodec, final VariableType<A> type) {
        this.elementCodec = elementCodec;
        this.type = type;
    }

    @Override
    public <T> DataResult<Pair<Field<A>, T>> decode(DynamicOps<T> ops, T input) {
        var dataResult = ops.getMap(input);
        if (dataResult.isSuccess()) {
            var variable = Variable.dispatchCodec(type).decode(ops, input);
            if (variable.isSuccess())
                return (DataResult<Pair<Field<A>, T>>) variable.result().map(pair -> {
                    if (pair.getFirst().variableType() != type)
                        return DataResult.<Pair<Field<A>, T>>error(() -> "Could not cast variable '" + pair.getFirst().id() + "' to '" + EnchantmentConfigRegistries.VARIABLE_TYPE.getKey(type) + "'. Expected " + EnchantmentConfigRegistries.VARIABLE_TYPE.getKey(pair.getFirst().variableType()) + ".");
                    return DataResult.success(Pair.of(new Field<>(pair.getFirst()), pair.getSecond()));
                }).or(() -> DataResult.<Pair<Field<A>, T>>error(() -> "Failed to decode VariableFieldCodec for unknown reasons.").error()).orElseThrow();
        }
        var valueResult = elementCodec.decode(ops, input).map(pair -> DataResult.success(Pair.of(new Field<>(pair.getFirst(), type), pair.getSecond())));
        if (valueResult.isSuccess())
            return valueResult.result().get();
        else
            return DataResult.error(() -> valueResult.error().get().message());
    }

    @Override
    public <T> DataResult<T> encode(Field<A> input, DynamicOps<T> ops, T prefix) {
        if (input.getInnerVariable() != null)
            return Variable.dispatchCodec(type).encode(input.getInnerVariable(), ops, prefix);
        return elementCodec.encode(input.getRawValue(), ops, prefix);
    }

    @Override
    public String toString() {
        return "VariableFieldCodec[" + elementCodec + ']';
    }

}