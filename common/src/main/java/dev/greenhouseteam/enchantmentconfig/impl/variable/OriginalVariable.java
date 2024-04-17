package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record OriginalVariable<T>(VariableType<T> variableType) implements Variable<T> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("original");

    public static MapCodec<OriginalVariable<Object>> staticCodec(VariableType<Object> variableType) {
        return MapCodec.unit(new OriginalVariable<>(variableType));
    }

    @Override
    public T getValue(Enchantment enchantment, ItemStack stack, T original) {
        return original;
    }

    @Override
    public MapCodec<OriginalVariable<Object>> codec(VariableType<Object> variableType) {
        return staticCodec(variableType);
    }

    public boolean isComparable() {
        return false;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
