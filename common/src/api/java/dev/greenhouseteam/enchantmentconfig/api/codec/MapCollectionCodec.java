package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MapCollectionCodec<K, V> implements Codec<Map<K, V>> {
    protected final String keyName;
    protected final String valueName;
    protected final Codec<K> keyCodec;
    protected final Codec<V> valueCodec;

    public MapCollectionCodec(String keyName, String valueName, Codec<K> keyCodec, Codec<V> valueCodec) {
        this.keyName = keyName;
        this.valueName = valueName;
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).flatMap(value -> {
            final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
            final ImmutableList.Builder<T> failed = ImmutableList.builder();
            final List<String> errorMessage = new ArrayList<>();

            final AtomicReference<DataResult<Unit>> result = new AtomicReference<>();
            result.setPlain(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));

            value.accept((val) -> {
                final DataResult<T> keyResult = ops.get(val, keyName);
                final DataResult<T> valueResult = ops.get(val, valueName);

                if (keyResult.result().isEmpty()) {
                    failed.add(val);
                    errorMessage.add("Failed to find '" + keyName + "' field");
                }

                if (valueResult.result().isEmpty()) {
                    failed.add(val);
                    errorMessage.add("Failed to find '" + valueName + "' field");
                }

                final DataResult<K> k = keyCodec.parse(ops, keyResult.result().get());
                final DataResult<V> v = valueCodec.parse(ops, valueResult.result().get());

                k.error().ifPresent(e -> {
                    failed.add(val);
                    errorMessage.add("Failed to decode '" + keyName + "' object");
                });
                v.error().ifPresent(e -> {
                    failed.add(val);
                    errorMessage.add("Failed to decode '" + valueName + "' object");
                });

                final DataResult<Pair<K, V>> readEntry = k.apply2stable(Pair::new, v);

                result.setPlain(result.getPlain().apply2stable((u, e) -> {
                    read.put(e.getFirst(), e.getSecond());
                    return u;
                }, readEntry));
            });

            final ImmutableMap<K, V> elements = read.build();
            final ImmutableList<T> errorList = failed.build();
            final T errors = ops.createList(errorList.stream());
            if (elements.isEmpty()) {
                return null;
            }

            final Pair<Map<K, V>, T> pair = Pair.of(elements, errors);

            DataResult<Pair<Map<K, V>, T>> retValue = result.getPlain().map(unit -> pair);
            if (!errorList.isEmpty()) {
                StringBuilder stringBuilder = errorMessageBuilder(errorList, errorMessage);
                retValue.setPartial(pair).mapError(s -> stringBuilder.toString());
            }
            return retValue;
        });
    }

    @NotNull
    protected static <T> StringBuilder errorMessageBuilder(ImmutableList<T> errorList, List<String> errorMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        if (errorList.size() > 1) {
            stringBuilder.append("Multiple errors found whilst decoding: \n");
        }
        for (int i = 0; i < errorMessage.size(); ++i) {
            stringBuilder.append(errorMessage.get(i));
            if (i == errorList.size() - 1)
                stringBuilder.append(".");
            else
                stringBuilder.append(";\n");

        }
        return stringBuilder;
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        final RecordBuilder<T> builder = ops.mapBuilder();

        for (final Map.Entry<K, V> pair : input.entrySet()) {
            builder.add(keyCodec.encodeStart(ops, pair.getKey()), valueCodec.encodeStart(ops, pair.getValue()));
        }

        return builder.build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapCollectionCodec<?, ?> mapCollectionCodec = (MapCollectionCodec<?, ?>)o;
        return Objects.equals(keyName, mapCollectionCodec.keyName) && Objects.equals(valueName, mapCollectionCodec.valueCodec) && Objects.equals(keyCodec, mapCollectionCodec.keyCodec) && Objects.equals(valueCodec, mapCollectionCodec.valueCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyName, valueName, keyCodec, valueCodec);
    }

    @Override
    public String toString() {
        return "MapCollectionCodec[" + keyName + ": " + keyCodec + ", " + valueName + ": " + valueCodec + ']';
    }
}