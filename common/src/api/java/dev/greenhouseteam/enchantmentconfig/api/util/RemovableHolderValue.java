package dev.greenhouseteam.enchantmentconfig.api.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record RemovableHolderValue<T>(Either<TagKey<T>, Holder<T>> either, boolean excluded) {
    public static <E> Codec<RemovableHolderValue<E>> codec(ResourceKey<? extends Registry<E>> registryKey, Codec<Holder<E>> codec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<RemovableHolderValue<E>, T>> decode(DynamicOps<T> ops, T input) {
                if (ops instanceof RegistryOps<?>) {
                    DataResult<String> data = ops.getStringValue(input);
                    if (data.isError())
                        return DataResult.error(() -> data.error().get().message());
                    String s = data.getOrThrow();
                    boolean removed = s.startsWith("-");
                    if (removed && s.charAt(1) == '#')
                        return DataResult.success(Pair.of(new RemovableHolderValue<>(Either.left(TagKey.create(registryKey, new ResourceLocation(s.substring(2)))), true), input));
                    else if (!removed && s.startsWith("#"))
                        return DataResult.success(Pair.of(new RemovableHolderValue<>(Either.left(TagKey.create(registryKey, new ResourceLocation(s.substring(1)))), false), input));

                    if (removed)
                        input = ops.createString(s.substring(1));
                    var holder = codec.decode(ops, input).getOrThrow();
                    return DataResult.success(Pair.of(new RemovableHolderValue<>(Either.right(holder.getFirst()), removed), input));
                }
                throw new UnsupportedOperationException("Could not decode RemovableHolderValue without RegistryOps.");
            }

            @Override
            public <T> DataResult<T> encode(RemovableHolderValue<E> input, DynamicOps<T> ops, T prefix) {
                StringBuilder stringBuilder = new StringBuilder();
                if (input.excluded())
                    stringBuilder.append("-");
                stringBuilder.append(input.either.mapBoth(tagKey -> "#" + tagKey.location(), holder -> holder.unwrapKey().map(key -> key.location().toString()).orElse("")));
                return DataResult.success(ops.createString(stringBuilder.toString()));
            }
        };
    }
}
