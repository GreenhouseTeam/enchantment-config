package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Basically the {@link MapCollectionCodec} class but allows users to either optionally specify
 * a primitive value, which will be placed at the key of 1, or "min" and "max" fields,
 * which allows users to specify a range.
 * This will fall back to MapCollectionCodec logic if either of the above aren't present.
 * @param <V> The type of any values inside the map.
 */
public class RangeAllowedIntegerMapCodec<V> extends MapCollectionCodec<Integer, V> {

    protected RangeAllowedIntegerMapCodec(String keyName, String elementName, Codec<V> elementCodec) {
        super(keyName, elementName, Codec.INT, elementCodec);
    }

    @Override
    public <T> DataResult<Pair<Map<Integer, V>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<V> parsed = valueCodec.parse(ops, input);
        DataResult<Pair<Map<Integer, V>, T>> pairParsed;
        final List<String> errorMessage = new ArrayList<>();
        if (parsed.error().isPresent()) {
            pairParsed = ops.getList(input).map(value -> {
                final ImmutableMap.Builder<Integer, V> read = ImmutableMap.builder();
                final ImmutableList.Builder<T> failed = ImmutableList.builder();

                final AtomicReference<DataResult<Unit>> result = new AtomicReference<>();
                result.setPlain(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));

                value.accept((val) -> {
                    final DataResult<T> minResult = ops.get(val, "min");
                    final DataResult<T> maxResult = ops.get(val, "max");
                    final DataResult<T> valueResult = ops.get(val, valueName);

                    if (minResult.isSuccess() || maxResult.isSuccess()) {
                        if (minResult.isError()) {
                            failed.add(val);
                            errorMessage.add("Failed to find 'min' field");
                        }

                        if (maxResult.isError()) {
                            failed.add(val);
                            errorMessage.add("Failed to find 'max' field");
                        }

                        if (valueResult.isError()) {
                            failed.add(val);
                            errorMessage.add("Failed to find '" + valueName + "' field");
                        }
                    } else {
                        final DataResult<T> keyResult = ops.get(val, keyName);

                        if (keyResult.result().isEmpty()) {
                            failed.add(val);
                            errorMessage.add("Failed to find '" + keyName + "' field");
                        }

                        if (valueResult.result().isEmpty()) {
                            failed.add(val);
                            errorMessage.add("Failed to find '" + valueName + "' field");
                        }

                        final DataResult<Integer> k = keyCodec.parse(ops, keyResult.result().get());
                        final DataResult<V> v = valueCodec.parse(ops, valueResult.result().get());

                        k.error().ifPresent(e -> {
                            failed.add(val);
                            errorMessage.add("Failed to decode '" + keyName + "' object");
                        });
                        v.error().ifPresent(e -> {
                            failed.add(val);
                            errorMessage.add("Failed to decode '" + valueName + "' object");
                        });

                        final DataResult<Pair<Integer, V>> readEntry = k.apply2stable(Pair::new, v);

                        result.setPlain(result.getPlain().apply2stable((u, e) -> {
                            read.put(e.getFirst(), e.getSecond());
                            return u;
                        }, readEntry));

                        return;
                    }

                    final DataResult<Integer> min = keyCodec.parse(ops, minResult.result().get());
                    final DataResult<Integer> max = keyCodec.parse(ops, maxResult.result().get());
                    final DataResult<V> v = valueCodec.parse(ops, valueResult.result().get());


                    min.error().ifPresent(e -> {
                        failed.add(val);
                        errorMessage.add("Failed to decode 'min' integer");
                    });
                    max.error().ifPresent(e -> {
                        failed.add(val);
                        errorMessage.add("Failed to decode 'max' integer");
                    });
                    v.error().ifPresent(e -> {
                        failed.add(val);
                        errorMessage.add("Failed to decode '" + valueName + "' object");
                    });

                    final DataResult<Pair<Integer, V>> minEntry = min.apply2stable(Pair::new, v);
                    final DataResult<Pair<Integer, V>> maxEntry = max.apply2stable(Pair::new, v);

                    result.setPlain(result.getPlain().apply2stable((u, e) -> {
                        read.put(e.getFirst(), e.getSecond());
                        return u;
                    }, minEntry));
                    result.setPlain(result.getPlain().apply2stable((u, e) -> {
                        read.put(e.getFirst(), e.getSecond());
                        return u;
                    }, maxEntry));
                });

                final ImmutableMap<Integer, V> elements = read.build();
                final ImmutableList<T> errorList = failed.build();
                final T errors = ops.createList(errorList.stream());

                final Pair<Map<Integer, V>, T> pair = Pair.of(elements, errors);

                if (elements.isEmpty()) {
                    return null;
                }

                DataResult<Pair<Map<Integer, V>, T>> retValue = result.getPlain().map(unit -> pair);
                if (!errorList.isEmpty()) {
                    StringBuilder stringBuilder = errorMessageBuilder(errorList, errorMessage);
                    retValue.setPartial(pair).mapError(s -> stringBuilder.toString());
                }
                return retValue;
            }).result().get();
        } else {
            pairParsed = parsed.map(v -> {
                ImmutableMap.Builder<Integer, V> map = new ImmutableMap.Builder<>();
                map.put(1, v);
                return Pair.of(map.build(), input);
            });
        }

        if (pairParsed.error().isPresent())
            return DataResult.error(() -> pairParsed.error().get().message());

        return pairParsed;
    }

    @Override
    public String toString() {
        return "RangedIntegerMapCodec[" + keyName + ": " + keyCodec + ", " + valueName + ": " + valueCodec + ']';
    }
}