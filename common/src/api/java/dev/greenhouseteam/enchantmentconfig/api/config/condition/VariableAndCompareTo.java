package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;

import java.util.stream.Stream;

public record VariableAndCompareTo<T>(EnchantmentVariable<T> variable, Field<T> compareTo) {
    public static class Codec extends MapCodec<VariableAndCompareTo<Object>> {
        public static final Codec INSTANCE = new Codec();

        @Override
        public <T> DataResult<VariableAndCompareTo<Object>> decode(DynamicOps<T> ops, MapLike<T> input) {
            T variableInput = input.get("variable");
            if (variableInput == null)
                return DataResult.error(() -> "Could not find 'variable' field.");
            var variableResult = EnchantmentVariable.CODEC.decode(ops, variableInput);
            if (variableResult.isError())
                return DataResult.error(() -> "Could not decode variable. " + variableResult.error().get().message());
            EnchantmentVariable<Object> variable = (EnchantmentVariable<Object>) variableResult.result().get().getFirst();
            T compareToField = input.get("compare_to");
            if (compareToField == null)
                return DataResult.error(() -> "Could not find 'compare_to' field.");
            var compareToResult = EnchantmentConfigCodecs.fieldCodec(variable.getComparisonValueCodec(), variable.getInnerClass()).decode(ops, compareToField);
            if (compareToResult.isError())
                return DataResult.error(() -> "Could not decode compare to value. " + compareToResult.error().get().message());
            return DataResult.success(new VariableAndCompareTo<>(variable, compareToResult.result().get().getFirst()));
        }

        @Override
        public <T> RecordBuilder<T> encode(VariableAndCompareTo<Object> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add("variable", EnchantmentVariable.CODEC.encodeStart(ops, input.variable()));
            prefix.add("compare_to", EnchantmentConfigCodecs.fieldCodec(input.variable().getComparisonValueCodec(), input.variable().getInnerClass()).encodeStart(ops, input.compareTo()));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("variable"), ops.createString("compare_to"));
        }
    }
}
