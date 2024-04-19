package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.mixin.EnchantmentAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public record MaxLevelVariable(Optional<Holder<Enchantment>> otherEnchantment, boolean unmodified, float percentage) implements SingleTypedVariable<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("max_level");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<MaxLevelVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("enchantment").forGetter(MaxLevelVariable::otherEnchantment),
            Codec.BOOL.optionalFieldOf("unmodified", false).forGetter(MaxLevelVariable::unmodified),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("percentage", 1.0F).forGetter(MaxLevelVariable::percentage)
    ).apply(inst, MaxLevelVariable::new));

    @Override
    public Integer getValue(Enchantment enchantment, ItemStack stack, Integer original) {
        if (otherEnchantment.isPresent() && !otherEnchantment.get().isBound())
            return 0;

        Enchantment finalEnchantment = otherEnchantment.map(Holder::value).orElse(enchantment);

        if (unmodified)
            return (int) (((EnchantmentAccessor)finalEnchantment).getDefinition().maxLevel() * percentage());
        return (int) (finalEnchantment.getMaxLevel() * percentage());
    }

    @Override
    public VariableSerializer<Integer, Integer> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Integer> {
        @Override
        public VariableType<Integer> type(VariableType<?> type) {
            return VariableTypes.INT;
        }

        @Override
        public MapCodec<MaxLevelVariable> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
