package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.mixin.api.accessor.OptionalFieldCodecAccessor;

import java.util.Locale;
import java.util.Optional;

public class DefaultableCodec<A> extends OptionalFieldCodec<A> {
    private static final String DEFAULT_KEY = "default";

    public DefaultableCodec(final String name, final Codec<A> elementCodec) {
        super(name, elementCodec);
    }

    @Override
    public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
        T value = input.get(((OptionalFieldCodecAccessor)this).enchantmentconfig$getName());
        if (value == null || value instanceof String str && (str.toLowerCase(Locale.ROOT).equals(DEFAULT_KEY))) {
            return DataResult.success(Optional.empty());
        }
        final DataResult<A> parsed = ((OptionalFieldCodecAccessor)this).enchantmentconfig$getElementCodec().parse(ops, value);
        if (parsed.error().isPresent()) {
            EnchantmentConfigUtil.LOGGER.error("Failed to parse field '{}'. You may use '{}' (case-insensitive) to get the default value. {}", ((OptionalFieldCodecAccessor)this).enchantmentconfig$getName(), DEFAULT_KEY, parsed.error().get().message());
        }
        return parsed.map(Optional::of);
    }

    @Override
    public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        if (input.isEmpty()) {
            prefix.add(((OptionalFieldCodecAccessor)this).enchantmentconfig$getName(), ops.createString("DEFAULT"));
            return prefix;
        }
        return super.encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "DefaultableCodec[" + ((OptionalFieldCodecAccessor)this).enchantmentconfig$getName() + ": " + ((OptionalFieldCodecAccessor)this).enchantmentconfig$getElementCodec() + ']';
    }

}