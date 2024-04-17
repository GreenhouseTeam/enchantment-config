package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.codec.VariableTypeCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;

import java.util.stream.Stream;

public record FieldPair<T>(Field<T> left, Field<T> right) {
    public static class Codec extends MapCodec<FieldPair<Object>> {
        private final String leftKey;
        private final String rightKey;
        private VariableType<?> type;

        public Codec(String leftKey, String rightKey, VariableType<?> type) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.type = type;
        }

        public Codec(String leftKey, String rightKey) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.type = null;
        }


        @Override
        public <T> DataResult<FieldPair<Object>> decode(DynamicOps<T> ops, MapLike<T> input) {
            Field<Object> left;
            Field<Object> right;

            T leftInput = input.get(leftKey);
            if (type == null) {
                var value = VariableTypeCodec.INSTANCE.decode(ops, ops.getMap(leftInput).getOrThrow());
                if (value.isSuccess())
                    type = value.result().get();
            }
            var leftResult = EnchantmentConfigCodecs.fieldCodec(type).decode(ops, leftInput);
            if (leftResult == null && type != null) {
                if (type.getDefaultComparisonValue() != null)
                    left = new Field<>(type.getDefaultComparisonValue(), (VariableType<Object>) type);
                else
                    return DataResult.error(() -> "Could not decode " + leftKey + "'. " + leftResult.error().get().message());
            } else
                left = (Field<Object>) leftResult.result().get().getFirst();
            T rightInput = input.get(rightKey);
            if (type == null) {
                var value = VariableTypeCodec.INSTANCE.decode(ops, ops.getMap(rightInput).getOrThrow());
                if (value.isSuccess())
                    type = value.result().get();
            }
            var rightResult = EnchantmentConfigCodecs.fieldCodec(type).decode(ops, rightInput);
            if (rightResult.isError() && type != null)
                if (type.getDefaultComparisonValue() != null)
                    right = new Field<>(type.getDefaultComparisonValue(), (VariableType<Object>) type);
                else
                    return DataResult.error(() -> "Could not decode '" + rightKey + "' value. " + rightResult.error().get().message());
            else
                right = (Field<Object>) rightResult.result().get().getFirst();
            if (type == null)
                return DataResult.error(() -> "Could not resolve value type.");
            // We need to set type to null here, so it resets in static instances.
            type = null;
            return DataResult.success(new FieldPair<>(left, right));
        }

        @Override
        public <T> RecordBuilder<T> encode(FieldPair<Object> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add(leftKey, EnchantmentConfigCodecs.fieldCodec(input.right().getVariableType()).encodeStart(ops, input.left()));
            prefix.add(rightKey, EnchantmentConfigCodecs.fieldCodec(input.right().getVariableType()).encodeStart(ops, input.right()));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(leftKey), ops.createString(rightKey));
        }
    }
}
