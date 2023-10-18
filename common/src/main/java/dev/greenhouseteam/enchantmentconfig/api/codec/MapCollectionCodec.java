package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MapCollectionCodec<K, V> implements Codec<Map<K, V>> {
    String keyName;
    String elementName;
    Codec<K> keyCodec;
    Codec<V> elementCodec;

    public MapCollectionCodec(String keyName, String elementName, Codec<K> keyCodec, Codec<V> elementCodec) {
        this.keyName = keyName;
        this.elementName = elementName;
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMapEntries(input).flatMap(map -> {
            final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
            final ImmutableMap.Builder<T, T> failed = ImmutableMap.builder();

            final AtomicReference<DataResult<Unit>> result = new AtomicReference<>();
            result.setPlain(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));

            map.accept((key, value) -> {
                final DataResult<K> k = keyCodec.parse(ops, key);
                final DataResult<V> v = elementCodec.parse(ops, value);

                final DataResult<Pair<K, V>> readEntry = k.apply2stable(Pair::new, v);

                readEntry.error().ifPresent(e -> EnchantmentConfigUtil.LOGGER.error("Failed to decode object '{}' inside MapCollectionCodec. {}", value, e));

                result.setPlain(result.getPlain().apply2stable((u, e) -> {
                    read.put(e.getFirst(), e.getSecond());
                    return u;
                }, readEntry));
            });

            final ImmutableMap<K, V> elements = read.build();
            final T errors = ops.createMap(failed.build());

            final Pair<Map<K, V>, T> pair = Pair.of(elements, errors);

            return result.getPlain().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        final RecordBuilder<T> builder = ops.mapBuilder();

        for (final Map.Entry<K, V> pair : input.entrySet()) {
            builder.add(keyCodec.encodeStart(ops, pair.getKey()), elementCodec.encodeStart(ops, pair.getValue()));
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
        return Objects.equals(keyName, mapCollectionCodec.keyName) && Objects.equals(elementName, mapCollectionCodec.elementName) && Objects.equals(keyCodec, mapCollectionCodec.keyCodec) && Objects.equals(elementCodec, mapCollectionCodec.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyName, elementName, keyCodec, elementCodec);
    }

    @Override
    public String toString() {
        return "MapCollectionCodec[" + keyName + ": " + keyCodec + ", " + elementName + ": " + elementCodec + ']';
    }
}