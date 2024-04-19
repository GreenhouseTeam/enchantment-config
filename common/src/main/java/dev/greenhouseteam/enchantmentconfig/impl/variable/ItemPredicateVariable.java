package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public record ItemPredicateVariable<I>(Field<I, Object> field, Optional<Field<I, Object>> elseField, ItemPredicate predicate) implements Variable<I, Object> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("item_predicate");
    public static final Serializer SERIALIZER = new Serializer();

    public static <T> MapCodec<ItemPredicateVariable<T>> staticCodec(VariableType<T> variableType) {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentConfigCodecs.inputFieldCodec(variableType).fieldOf("value").forGetter(ItemPredicateVariable::field),
                EnchantmentConfigCodecs.inputFieldCodec(variableType).optionalFieldOf("else").forGetter(ItemPredicateVariable::elseField),
                ItemPredicate.CODEC.fieldOf("predicate").forGetter(ItemPredicateVariable::predicate)
        ).apply(inst, ItemPredicateVariable::new));
    }

    @Override
    public Object getValue(Enchantment enchantment, ItemStack stack, I original) {
        if (predicate.test(stack))
            return field.get(enchantment, stack, original);
        else if (elseField.isPresent())
            return elseField.get().get(enchantment, stack, original);
        return original;
    }

    public boolean allowedInRootCondition() {
        return false;
    }

    @Override
    public VariableSerializer<I, Object> getSerializer() {
        return (VariableSerializer<I, Object>) SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Object> {

        @Override
        public VariableType<Object> type(VariableType<?> inputType) {
            return (VariableType<Object>) inputType;
        }

        @Override
        public MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType) {
            return staticCodec(variableType);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
