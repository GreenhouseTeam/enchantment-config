package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Map;

public class LevelKeyMapCodec<V> extends MapCollectionCodec<java.lang.Integer, V> {

    public LevelKeyMapCodec(String keyName, String elementName, Codec<V> elementCodec) {
        super(keyName, elementName, EnchantmentConfigCodecs.INT, elementCodec);
    }

    @Override
    public <T> DataResult<Pair<Map<Integer, V>, T>> decode(DynamicOps<T> ops, T input) {
        return elementCodec.parse(ops, input)
                .result()
                .map(value -> {

                    ImmutableMap.Builder<Integer, V> map = new ImmutableMap.Builder<>();

                    map.put(1, value);

                    return DataResult.success(Pair.of((Map<Integer, V>)map.build(), input));
                })
                .orElse(super.decode(ops, input));
    }

    @Override
    public String toString() {
        return "LevelKeyMapCodec[" + keyName + ": " + keyCodec + ", " + elementName + ": " + elementCodec + ']';
    }
}