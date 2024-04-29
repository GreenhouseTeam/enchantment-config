package dev.greenhouseteam.enchantmentconfig.impl.data;

import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import dev.greenhouseteam.enchantmentconfig.mixin.HolderSetNamedAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchantmentConfigTagLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {

    private final Registry<T> registry;
    private Set<Holder.Reference<T>> resetHolders = new HashSet<>();

    public EnchantmentConfigTagLookup(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public HolderLookup.RegistryLookup<T> parent() {
        return registry.asLookup();
    }

    @Override
    public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
        return Optional.of(this.getOrThrow(tagKey));
    }

    // This really is a mess, but we need to sidestep tag getting somehow.
    @Override
    public HolderSet.Named<T> getOrThrow(TagKey<T> tagKey) {
        HolderSet.Named<T> named = registry.getOrCreateTag(tagKey);
        var optional = EnchantmentConfig.getTags().stream().filter(loadResult -> loadResult.tags().getOrDefault(tagKey.location(), null) != null).findAny();
        if (optional.isEmpty())
            return named;
        ((HolderSetNamedAccessor) named).enchantmentconfig$bind(List.copyOf(optional.get().tags().get(tagKey.location())));
        named.stream().forEach(holder -> {
            if (holder instanceof Holder.Reference<T> reference) {
                if (!resetHolders.contains(holder))
                    reference.bindTags(Set.of());
                Set<TagKey<T>> tagKeys = holder.tags().collect(Collectors.toSet());
                tagKeys.add(named.key());
                reference.bindTags(tagKeys);
                resetHolders.add(reference);
            }
        });
        return named;
    }

    public void resetHolders() {
        resetHolders.clear();
    }
}