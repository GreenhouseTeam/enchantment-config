package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Map;
import java.util.Optional;

public class EnchantmentConfigCodecs {

    /**
     * An integer codec that will error upon fail.
     * Prefer using this over {@link Codec#INT}
     */
    public static PrimitiveCodec<Integer> INT = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Integer> read(DynamicOps<T> ops, T input) {
            DataResult<Integer> decoded = Codec.INT.parse(ops, input);
            if (decoded.error().isPresent()) {
                return DataResult.error(() -> "Failed to parse int value from input '" + input + "'.");
            }
            return decoded;
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Integer value) {
            return ops.createInt(value);
        }

        @Override
        public String toString() {
            return "EnchantmentConfigInt";
        }
    };

    /**
     * A boolean codec that will error upon fail.
     * Prefer using this over {@link Codec#BOOL}
     */
    public static PrimitiveCodec<Boolean> BOOLEAN = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Boolean> read(DynamicOps<T> ops, T input) {
            DataResult<Boolean> decoded = Codec.BOOL.parse(ops, input);
            if (decoded.error().isPresent()) {
                return DataResult.error(() -> "Failed to parse boolean value from input '" + input + "'.");
            }
            return decoded;
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Boolean value) {
            return ops.createBoolean(value);
        }

        @Override
        public String toString() {
            return "EnchantmentConfigBoolean";
        }
    };

    /**
     * A string codec that will error upon fail.
     * Prefer using this over {@link Codec#STRING}
     */
    public static PrimitiveCodec<String> STRING = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<String> read(DynamicOps<T> ops, T input) {
            DataResult<String> decoded = Codec.STRING.parse(ops, input);
            if (decoded.error().isPresent()) {
                return DataResult.error(() -> "Failed to parse string value from input '" + input + "'.");
            }
            return decoded;
        }

        @Override
        public <T> T write(DynamicOps<T> ops, String value) {
            return ops.createString(value);
        }

        @Override
        public String toString() {
            return "EnchantmentConfigString";
        }
    };

    /**
     * A codec that allows specifying 'DEFAULT' (case-insensitive)
     * to return {@link Optional#empty()}.
     *
     *
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

    public static <T> Codec<HolderSet<T>> tagOrElementCodec(ResourceKey<Registry<T>> registryKey) {
        RegistryAccess registryAccess = IEnchantmentConfigGetter.INSTANCE.getRegistryAccess();
        if (registryAccess == null) {
            return Codec.unit(HolderSet.direct());
        }
        Optional<Registry<T>> optionalRegistry = registryAccess.registry(registryKey);
        if (optionalRegistry.isEmpty()) {
            return Codec.unit(HolderSet.direct());
        }
        Registry<T> registry = optionalRegistry.get();
        return Codec.either(TagKey.hashedCodec(registryKey), registry.holderByNameCodec())
                .xmap(tagKeyHolderEither -> tagKeyHolderEither.map(registry::getOrCreateTag, HolderSet::direct),
                        holders -> {
                            if (holders.unwrapKey().isEmpty()) {
                                return Either.right(holders.get(0));
                            }
                            return Either.left(holders.unwrapKey().get());
                        });
    }

}
