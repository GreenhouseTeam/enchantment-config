package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;

import java.util.Map;
import java.util.stream.Stream;

public class ExtraFieldsCodec extends MapCodec<Map<String, ?>> {
    private final Map<String, ExtraFieldType<?>> fields;

    public ExtraFieldsCodec(Map<String, ExtraFieldType<?>> fields) {
        this.fields = fields;
    }

    @Override
    public <T> DataResult<Map<String, ?>> decode(DynamicOps<T> ops, MapLike<T> input) {
        final ImmutableMap.Builder<String, Object> read = ImmutableMap.builder();

        for (Map.Entry<String, ExtraFieldType<?>> field : fields.entrySet()) {
            T value = input.get(field.getKey());
            DataResult<Pair<?, T>> result = field.getValue().objectCodec().decode(ops, value).map(pair -> pair);

            if (result.error().isPresent()) {
                EnchantmentConfigUtil.LOGGER.error("Failed to decode extra field '{}' inside ExtraFieldsCodec. (Skipping): {}", field.getKey(), result.error().get());
                continue;
            }

            read.put(field.getKey(), result.result().get().getFirst());
        }

        return DataResult.success(read.build());
    }

    @Override
    public <T> RecordBuilder<T> encode(Map<String, ?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        final RecordBuilder<T> builder = ops.mapBuilder();

        for (Map.Entry<String, ExtraFieldType<?>> field : fields.entrySet()) {
            DataResult<T> result = field.getValue().objectCodec().encodeStart(ops, input.get(field.getKey()));
            result.error().ifPresent(e -> EnchantmentConfigUtil.LOGGER.error("Failed to encode extra field '{}' inside ExtraFieldsCodec. (Skipping): {}", field.getKey(), e));
            if (result.isSuccess())
                builder.add(field.getKey(), result);
        }

        return builder;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return fields.keySet().stream().map(ops::createString);
    }
}
