package dev.greenhouseteam.enchantmentconfig.api.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
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

    public static <T> ExcludableHolderSet<T> empty() {
        return new ExcludableHolderSet<>(null, null, List.of());
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

    public ExcludableHolderSet<T> merge(Optional<ExcludableHolderSet<T>> old) {
        return merge(old, Optional.empty());
    }

    public ExcludableHolderSet<T> merge(Optional<ExcludableHolderSet<T>> old, Optional<ExcludableHolderSet<T>> global) {
        List<RemovableHolderValue<T>> newBase = new ArrayList<>();
        global.ifPresent(holders -> newBase.addAll(holders.baseValues));
        if (old.isPresent()) {
            newBase.removeIf(value -> value.excluded() && old.get().baseValues.stream().anyMatch(value1 -> !value1.excluded() && value1.either().equals(value.either())));
            newBase.addAll(old.get().baseValues);
        }
        newBase.removeIf(value -> value.excluded() && baseValues.stream().anyMatch(value1 -> !value1.excluded() && value1.either().equals(value.either())));
        newBase.addAll(baseValues);
        baseValues = newBase;
        return this;
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    public String toString() {
        return "ExcludableSet[" + this.contents() + "] -[" + this.exclusions() + "]";
    }

    public boolean canSerializeIn(HolderOwner<T> owner) {
        if (this.owner == null)
            return true;
        return this.owner.canSerializeIn(owner);
    }
}
