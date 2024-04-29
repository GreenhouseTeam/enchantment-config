package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Comparison;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsPredicateAccess;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public class ItemPredicateVariable<I> implements Variable<I, Object> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("item_predicate");
    public static final Serializer SERIALIZER = new Serializer();

    public static <T> MapCodec<ItemPredicateVariable<T>> staticCodec(VariableType<T> variableType) {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentConfigCodecs.inputFieldCodec(variableType).fieldOf("value").forGetter(var -> var.field),
                EnchantmentConfigCodecs.inputFieldCodec(variableType).optionalFieldOf("else").forGetter(var -> var.elseField),
                ItemPredicate.CODEC.fieldOf("predicate").forGetter(var -> var.predicate),
                Comparison.CODEC.optionalFieldOf("comparison", Comparison.EQUAL).forGetter(var -> var.comparison)
        ).apply(inst, ItemPredicateVariable::new));
    }

    private final Field<I, Object> field;
    private final Optional<Field<I, Object>> elseField;
    private final ItemPredicate predicate;
    private final Comparison comparison;
    private boolean hasLoggedError = false;

    public ItemPredicateVariable(Field<I, Object> field, Optional<Field<I, Object>> elseField,
                                 ItemPredicate predicate, Comparison comparison) {
        this.field = field;
        this.elseField = elseField;
        this.predicate = predicate;
        this.comparison = comparison;
        this.predicate.subPredicates().forEach((type, itemSubPredicate) -> {
            // Avoid StackOverflowError the only way I know how.
            if (itemSubPredicate instanceof ItemEnchantmentsPredicate enchantmentsPredicate)
                ((ItemEnchantmentsPredicateAccess)enchantmentsPredicate).enchantmentconfig$setNoConfigs();
        });
    }

    @Override
    public Object getValue(Enchantment enchantment, ItemStack stack, I original) {
        try {
            if (comparison.compare(predicate.test(stack), true))
                return field.get(enchantment, stack, original);
            else if (elseField.isPresent())
                return elseField.get().get(enchantment, stack, original);
        } catch (UnsupportedOperationException ex) {
            if (!hasLoggedError) {
                EnchantmentConfigApi.LOGGER.error("Could not handle {} item predicate with comparison {}. Returning original value.", ItemPredicate.CODEC.encodeStart(JsonOps.INSTANCE, predicate).result().get(), comparison, ex);
                hasLoggedError = true;
            }
        }
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
