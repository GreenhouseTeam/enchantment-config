package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.OptionalFieldCodec;

import java.util.Locale;
import java.util.Optional;

public class DefaultableCodec<A> extends OptionalFieldCodec<A> {
    private static final String DEFAULT_KEY = "default";
    private final String capturedName;
    private final Codec<A> capturedCodec;

    public DefaultableCodec(final String name, final Codec<A> elementCodec) {
        super(name, elementCodec, false);
        this.capturedName = name;
        this.capturedCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
        T value = input.get(capturedName);
        if (value == null || value instanceof String str && (str.toLowerCase(Locale.ROOT).equals(DEFAULT_KEY))) {
            return DataResult.success(Optional.empty());
        }
        return super.decode(ops, input);
    }

    @Override
    public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        if (input.isEmpty()) {
            prefix.add(capturedName, ops.createString("DEFAULT"));
            return prefix;
        }
        return super.encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "DefaultableCodec[" + capturedName + ": " + capturedCodec + ']';
    }

}