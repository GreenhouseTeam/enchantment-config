package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.mixin.EnchantmentAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record MaxLevelVariable(boolean unmodified, float percentage) implements EnchantmentVariable<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("max_level");
    public static final MapCodec<MaxLevelVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.optionalFieldOf("unmodified", false).forGetter(MaxLevelVariable::unmodified),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("percentage", 1.0F).forGetter(MaxLevelVariable::percentage)
    ).apply(inst, MaxLevelVariable::new));

    @Override
    public Integer getValue(Enchantment enchantment, ItemStack stack) {
        if (unmodified())
            return (int) Mth.lerp(percentage(), 0, ((EnchantmentAccessor)enchantment).getDefinition().maxLevel());
        return (int) Mth.lerp(percentage(), 0, enchantment.getMaxLevel());
    }

    @Override
    public Class<Integer> getInnerClass() {
        return Integer.class;
    }

    @Override
    public MapCodec<? extends EnchantmentVariable<?>> codec() {
        return CODEC;
    }
}
