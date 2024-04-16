package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

// TODO: Document this class.
public interface EnchantmentVariable<T> {
    Codec<EnchantmentVariable<?>> CODEC = EnchantmentConfigRegistries.ENCHANTMENT_VARIABLE_CODEC.byNameCodec().dispatch(EnchantmentVariable::codec, codec -> codec);

    T getValue(Enchantment enchantment, ItemStack stack, T original);

    default T getDefaultComparisonValue() {
        return null;
    }

    Class<T> getInnerClass();

    MapCodec<? extends EnchantmentVariable<?>> codec();

    default boolean isComparable() {
        return true;
    }

    default Codec<T> getComparisonValueCodec() {
        if (!isComparable()) {
            return null;
        }
        throw new NullPointerException("Value is not allowed to be compared.");
    }
}
