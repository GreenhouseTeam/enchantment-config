package dev.greenhouseteam.enchantmentconfig.api.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class ExcludableHolderSet<T> extends HolderSet.ListBacked<T> {
    private final HolderOwner<T> owner;
    private final HolderGetter<T> getter;
    private List<RemovableHolderValue<T>> baseValues;

    private boolean context = false;

    public ExcludableHolderSet(HolderOwner<T> owner, HolderGetter<T> getter,
                               List<RemovableHolderValue<T>> baseValues) {
        this.owner = owner;
        this.getter = getter;
        this.baseValues = baseValues;
    }

    public void setContext(boolean value) {
        this.context = value;
    }

    public List<RemovableHolderValue<T>> getBaseValues() {
        return List.copyOf(baseValues);
    }

    @Override
    protected List<Holder<T>> contents() {
        return baseValues.stream().filter(value -> !value.excluded()).flatMap(
                value -> value.either().map(tagKey -> getter.get(tagKey).map(HolderSet.ListBacked::stream).orElse(Stream.of()), Stream::of)).toList();
    }

    protected List<Holder<T>> exclusions() {
        return baseValues.stream().filter(RemovableHolderValue::excluded).flatMap(
                value -> value.either().map(tagKey -> getter.get(tagKey).map(HolderSet.ListBacked::stream).orElse(Stream.of()), Stream::of)).toList();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(contents());
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return contents().contains(holder) || context && !exclusions().contains(holder);
    }

    public boolean inverseContains(Holder<T> holder) {
        return exclusions().contains(holder) || context && !contents().contains(holder);
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    public String toString() {
        return "ExcludableSet[" + this.contents() + "] -[" + this.exclusions() + "]";
    }

    public boolean canSerializeIn(HolderOwner<T> owner) {
        return this.owner.canSerializeIn(owner);
    }
}
