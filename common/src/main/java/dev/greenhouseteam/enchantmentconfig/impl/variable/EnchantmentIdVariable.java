package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentIdVariable implements EnchantmentVariable<ResourceKey<Enchantment>> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("enchantment_id");
    public static final MapCodec<EnchantmentIdVariable> CODEC = MapCodec.unit(EnchantmentIdVariable::new);

    @Override
    public ResourceKey<Enchantment> getValue(Enchantment enchantment, ItemStack stack, ResourceKey<Enchantment> original) {
        return BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment).orElseThrow(() -> new NullPointerException("Enchantment does not have a ResourceKey."));
    }

    @Override
    public Class<ResourceKey<Enchantment>> getInnerClass() {
        return castClass(ResourceKey.class);
    }

    private static <T> Class<T> castClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    @Override
    public Codec<ResourceKey<Enchantment>> getComparisonValueCodec() {
        return ResourceKey.codec(Registries.ENCHANTMENT);
    }

    @Override
    public MapCodec<? extends EnchantmentVariable<?>> codec() {
        return CODEC;
    }
}
