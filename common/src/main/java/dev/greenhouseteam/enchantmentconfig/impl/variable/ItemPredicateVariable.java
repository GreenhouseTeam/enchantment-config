package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public record ItemPredicateVariable<T>(Field<T> field, Optional<Field<T>> elseField, ItemPredicate predicate) implements Variable<T> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("item_predicate");

    public static MapCodec<ItemPredicateVariable<Object>> staticCodec(VariableType<Object> variableType) {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentConfigCodecs.fieldCodec(variableType).fieldOf("value").forGetter(ItemPredicateVariable::field),
                EnchantmentConfigCodecs.fieldCodec(variableType).optionalFieldOf("else").forGetter(ItemPredicateVariable::elseField),
                ItemPredicate.CODEC.fieldOf("predicate").forGetter(ItemPredicateVariable::predicate)
        ).apply(inst, ItemPredicateVariable::new));
    }

    @Override
    public T getValue(Enchantment enchantment, ItemStack stack, T original) {
        if (predicate().test(stack))
            return field.get(enchantment, stack, original);
        else if (elseField.isPresent())
            return elseField.get().get(enchantment, stack, original);
        return original;
    }

    public boolean isComparable() {
        return false;
    }

    @Override
    public MapCodec<ItemPredicateVariable<Object>> codec(VariableType<Object> variableType) {
        return staticCodec(variableType);
    }

    @Override
    public VariableType<T> variableType() {
        return field().getVariableType();
    }

    public boolean isVariedVariableType() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
