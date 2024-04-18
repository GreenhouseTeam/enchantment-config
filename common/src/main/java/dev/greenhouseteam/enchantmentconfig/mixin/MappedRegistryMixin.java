package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {

    @Shadow public abstract ResourceKey<? extends Registry<T>> key();

    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow @Nullable public abstract T get(@Nullable ResourceLocation resourceLocation);

    @Shadow public abstract Optional<Holder.Reference<T>> getHolder(ResourceLocation resourceLocation);

    @Shadow public abstract Optional<Holder.Reference<T>> getHolder(ResourceKey<T> resourceKey);

    @ModifyArg(method = "keySet", at = @At(value = "INVOKE", target = "Ljava/util/Collections;unmodifiableSet(Ljava/util/Set;)Ljava/util/Set;"))
    private Set<ResourceLocation> enchantmentconfig$disableFromKeySet(Set<ResourceLocation> original) {
        if (this.key() != (ResourceKey<? extends Registry<?>>) Registries.ENCHANTMENT)
            return original;

        return original.stream().filter(t -> {
            var optionalHolder = this.getHolder(t);
            if (optionalHolder.isPresent() && optionalHolder.get().is((TagKey<T>) EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                return false;
            return true;
        }).collect(Collectors.toSet());
    }

    @ModifyArg(method = "registryKeySet", at = @At(value = "INVOKE", target = "Ljava/util/Collections;unmodifiableSet(Ljava/util/Set;)Ljava/util/Set;"))
    private Set<ResourceKey<T>> enchantmentconfig$disableFromRegistryKeySet(Set<ResourceKey<T>> original) {
        if (this.key() != (ResourceKey<? extends Registry<?>>) Registries.ENCHANTMENT)
            return original;

        return original.stream().filter(t -> {
            var optionalHolder = this.getHolder(t);
            if (optionalHolder.isPresent() && optionalHolder.get().is((TagKey<T>) EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                return false;
            return true;
        }).collect(Collectors.toSet());
    }

    @ModifyArg(method = "entrySet", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;transformValues(Ljava/util/Map;Lcom/google/common/base/Function;)Ljava/util/Map;"))
    private Map<ResourceKey<T>, Holder.Reference<T>> enchantmentconfig$disableFromEntrySet(Map<ResourceKey<T>, Holder.Reference<T>> original) {
        if (this.key() != (ResourceKey<? extends Registry<?>>) Registries.ENCHANTMENT)
            return original;

        return original.entrySet().stream().filter(entry -> entry.getValue().is((TagKey<T>) EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @ModifyReturnValue(method = "holders", at = @At("RETURN"))
    private Stream<Holder.Reference<T>> enchantmentconfig$disableFromHolders(Stream<Holder.Reference<T>> original) {
        return original.filter(ref -> !ref.key().isFor(Registries.ENCHANTMENT) || !ref.is((TagKey<T>) EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG));
    }

    @ModifyArg(method = "iterator", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Iterators;transform(Ljava/util/Iterator;Lcom/google/common/base/Function;)Ljava/util/Iterator;"))
    private Iterator<T> enchantmentconfig$disableFromIterator(Iterator<T> original) {
        if (this.key() != (ResourceKey<? extends Registry<?>>) Registries.ENCHANTMENT)
            return original;

        // This is less performant, but we do it this way just in case new values are put into the iterator.
        List<T> list = new ArrayList<>();
        while (original.hasNext()) {
            T it = original.next();
            if (it instanceof Holder.Reference<?> reference && !reference.key().isFor(Registries.ENCHANTMENT) && reference.is((TagKey)EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                list.add(it);
        }
        return list.iterator();
    }
}
