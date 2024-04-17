package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;

import java.util.Optional;

public class EnchantmentConfigCodecs {
    /**
     * A codec that allows specifying 'DEFAULT' (case-insensitive)
     * to return {@link Optional#empty()}.
     *
     * @param fieldName The name of the field for parsing.
     * @param codec     The codec to use outside the default.
     * @return          A new {@link DefaultableCodec}
     * @param <T>       The type of the codec.
     */
    public static <T> MapCodec<Optional<T>> defaultableCodec(String fieldName, Codec<T> codec) {
        return new DefaultableCodec<>(fieldName, codec);
    }

    public static <K, V> MapCollectionCodec<K, V> mapCollectionCodec(String keyName, String valueName, Codec<K> keyCodec, Codec<V> valueCodec) {
        return new MapCollectionCodec<>(keyName, valueName, keyCodec, valueCodec);
    }

    public static <V> MapCollectionCodec<Integer, V> rangeAllowedIntegerCodec(String keyName, String valueName, Codec<V> valueCodec) {
        return new RangeAllowedIntegerMapCodec<>(keyName, valueName, valueCodec);
    }

    public static <T> FieldCodec<T> fieldCodec(VariableType<T> type) {
        return new FieldCodec<>(type.getValueCodec(), type);
    }

}
