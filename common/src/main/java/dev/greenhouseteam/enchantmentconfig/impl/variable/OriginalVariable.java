package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class OriginalVariable implements EnchantmentVariable<Object> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("original");
    public static final MapCodec<OriginalVariable> CODEC = MapCodec.unit(OriginalVariable::new);

    @Override
    public Object getValue(Enchantment enchantment, ItemStack stack, Object original) {
        return original;
    }

    @Override
    public Class<Object> getInnerClass() {
        return Object.class;
    }

    @Override
    public MapCodec<OriginalVariable> codec() {
        return CODEC;
    }

    public boolean isComparable() {
        return false;
    }
}
