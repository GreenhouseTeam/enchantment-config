package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentLevelVariable(Holder<Enchantment> otherEnchantment) implements SingleTypedVariable<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("enchantment_level");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<EnchantmentLevelVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().fieldOf("enchantment").forGetter(EnchantmentLevelVariable::otherEnchantment)
    ).apply(inst, EnchantmentLevelVariable::new));

    @Override
    public Integer getValue(Enchantment enchantment, ItemStack stack, Integer original) {
        if (otherEnchantment.isBound())
            return stack.getEnchantments().getLevel(otherEnchantment.value());
        return 0;
    }

    public boolean canRootCondition() {
        return false;
    }

    @Override
    public VariableSerializer<Integer, Integer> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Integer> {
        @Override
        public VariableType<Integer> type(VariableType<?> inputType) {
            return VariableTypes.INT;
        }

        @Override
        public MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
