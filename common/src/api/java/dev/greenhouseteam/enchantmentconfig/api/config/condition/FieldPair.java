package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;

import java.util.stream.Stream;

public record FieldPair<I, O>(Field<I, O> left, Field<I, O> right) {
    public static class Codec extends MapCodec<FieldPair<Object, Object>> {
        private final String leftKey;
        private final String rightKey;
        private final VariableType<?> baseType;
        private VariableType<?> type;

        public Codec(String leftKey, String rightKey, VariableType<?> baseType) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.baseType = baseType;
            this.type = baseType;
        }

        public Codec(String leftKey, String rightKey) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.baseType = null;
            this.type = null;
        }


        @Override
        public <T> DataResult<FieldPair<Object, Object>> decode(DynamicOps<T> ops, MapLike<T> input) {
            Field<Object, Object> left;
            Field<Object, Object> right;

            T leftInput = input.get(leftKey);
            var leftResult = EnchantmentConfigCodecs.fieldCodec(type).decode(ops, leftInput);
            if (leftResult == null && type != null) {
                if (type.getDefaultComparisonValue() != null)
                    left = new Field<>(type.getDefaultComparisonValue(), (VariableType<Object>) type);
                else
                    return DataResult.error(() -> "Could not decode " + leftKey + "'. " + leftResult.error().get().message());
            } else {
                left = (Field<Object, Object>) leftResult.result().get().getFirst();
                if (type == null)
                    type = left.getInputType();
            }
            T rightInput = input.get(rightKey);
            var rightResult = EnchantmentConfigCodecs.fieldCodec(type).decode(ops, rightInput);
            if (rightResult.isError() && type != null)
                if (type.getDefaultComparisonValue() != null)
                    right = new Field<>(type.getDefaultComparisonValue(), (VariableType<Object>) type);
                else
                    return DataResult.error(() -> "Could not decode '" + rightKey + "' value. " + rightResult.error().get().message());
            else
                right = (Field<Object, Object>) rightResult.result().get().getFirst();
            if (type == null)
                return DataResult.error(() -> "Could not resolve value type.");
            // We need to set type to null here, so it resets in static instances.
            type = baseType;
            return DataResult.success(new FieldPair<>(left, right));
        }

        @Override
        public <T> RecordBuilder<T> encode(FieldPair<Object, Object> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add(leftKey, EnchantmentConfigCodecs.fieldCodec(input.right().getOutputType()).encodeStart(ops, input.left()));
            prefix.add(rightKey, EnchantmentConfigCodecs.fieldCodec(input.right().getOutputType()).encodeStart(ops, input.right()));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(leftKey), ops.createString(rightKey));
        }
    }
}
