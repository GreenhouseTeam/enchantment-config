package dev.greenhouseteam.enchantmentconfig.api.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.greenhouseteam.enchantmentconfig.api.util.ExcludableHolderSet;
import dev.greenhouseteam.enchantmentconfig.api.util.RemovableHolderValue;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

public class ExcludableHolderSetCodec<T> implements Codec<ExcludableHolderSet<T>> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Codec<List<RemovableHolderValue<T>>> registryAwareCodec;

    private static <T> Codec<List<RemovableHolderValue<T>>> homogenousList(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> codec) {
        Codec<RemovableHolderValue<T>> eitherCodec = RemovableHolderValue.codec(registryKey, codec);
        Codec<List<RemovableHolderValue<T>>> codec2 = eitherCodec.listOf();
        return Codec.either(codec2, eitherCodec)
        .xmap(either -> either.map(list -> list, List::of), list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list));
    }

    protected ExcludableHolderSetCodec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> elementCodec) {
        this.registryKey = registryKey;
        this.registryAwareCodec = homogenousList(registryKey, elementCodec);
    }

    @Override
    public <TOps> DataResult<Pair<ExcludableHolderSet<T>, TOps>> decode(DynamicOps<TOps> ops, TOps input) {
        if (ops instanceof RegistryOps<TOps> registryOps) {
            Optional<HolderOwner<T>> ownerOptional = registryOps.owner(registryKey);
            Optional<HolderGetter<T>> optional = registryOps.getter(this.registryKey);
            if (optional.isPresent() && ownerOptional.isPresent()) {
                HolderGetter<T> holderGetter = optional.get();
                return this.registryAwareCodec
                        .decode(ops, input)
                        .flatMap(
                                pair -> {
                                    DataResult<ExcludableHolderSet<T>> dataResult = lookup(ownerOptional.get(), holderGetter, pair.getFirst());
                                    return dataResult.map(holderSet -> Pair.of(holderSet, pair.getSecond()));
                                }
                        );
            }
        }

        throw new UnsupportedOperationException("Could not decode ExcludableHolderSet without RegistryOps.");
    }
    private static <T> DataResult<ExcludableHolderSet<T>> lookup(HolderOwner<T> owner, HolderGetter<T> getter, List<RemovableHolderValue<T>> values) {
        return DataResult.success(new ExcludableHolderSet<>(owner, getter, values));
    }

    @Override
    public <TOps> DataResult<TOps> encode(ExcludableHolderSet<T> input, DynamicOps<TOps> ops, TOps prefix) {
        return registryAwareCodec.encode(input.getBaseValues(), ops, prefix);
    }
}
