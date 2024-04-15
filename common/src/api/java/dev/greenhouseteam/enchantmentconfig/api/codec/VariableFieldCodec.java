
package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.field.VariableField;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;

import java.util.Locale;
import java.util.Optional;

public class VariableFieldCodec<A> implements Codec<VariableField<A>> {
    private final Codec<A> elementCodec;
    private final Class<A> castClass;

    protected VariableFieldCodec(final Codec<A> elementCodec, final Class<A> castClass) {
        this.elementCodec = elementCodec;
        this.castClass = castClass;
    }

    @Override
    public <T> DataResult<Pair<VariableField<A>, T>> decode(DynamicOps<T> ops, T input) {
        var dataResult = ops.getMap(input);
        if (dataResult.isSuccess()) {
            var variable = EnchantmentVariable.CODEC.decode(ops, input);
            if (variable.isSuccess())
                return (DataResult<Pair<VariableField<A>, T>>) variable.result().map(pair -> {
                    if (!pair.getFirst().getInnerClass().isAssignableFrom(castClass))
                        return DataResult.<Pair<VariableField<A>, T>>error(() -> "Could not cast variable '" + EnchantmentConfigRegistries.ENCHANTMENT_VARIABLE_CODEC.getKey(pair.getFirst().codec()) + "' to '" + castClass.getName() + "'. Expected " + pair.getFirst().getInnerClass().getName() + ".");
                    return DataResult.success(Pair.of(new VariableField<>(pair.getFirst()), pair.getSecond()));
                }).orElse(DataResult.<Pair<VariableField<A>, T>>error(() -> "Failed to decode VariableFieldCodec for unknown reasons."));
        }
        var valueResult = elementCodec.decode(ops, input).map(pair -> DataResult.success(Pair.of(new VariableField<>(pair.getFirst()), pair.getSecond())));
        if (valueResult.isSuccess())
            return valueResult.result().get();
        else
            return DataResult.error(() -> valueResult.error().get().message());
    }

    @Override
    public <T> DataResult<T> encode(VariableField<A> input, DynamicOps<T> ops, T prefix) {
        if (input.getInnerVariable() != null)
            return EnchantmentVariable.CODEC.encode(input.getInnerVariable(), ops, prefix);
        return elementCodec.encode(input.getRawValue(), ops, prefix);
    }

    @Override
    public String toString() {
        return "VariableFieldCodec[" + elementCodec + ']';
    }

}