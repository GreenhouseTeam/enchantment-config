package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Comparison;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.FieldPair;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class ConditionalVariable<I, O> implements Variable<I, Boolean> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("conditional");
    public static final Serializer SERIALIZER = new Serializer();
    public static MapCodec<ConditionalVariable<Object, Object>> staticCodec(@Nullable VariableType<Object> contextType) {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                new FieldPair.Codec("value", "compare_to", contextType).forGetter(variable -> variable.fieldPair),
                Comparison.CODEC.optionalFieldOf("comparison", Comparison.EQUAL).forGetter(variable -> variable.comparison)
        ).apply(inst, ConditionalVariable::new));
    }

    private final FieldPair<I, O> fieldPair;
    private final Comparison comparison;
    private boolean hasLoggedError = false;

    public ConditionalVariable(FieldPair<I, O> fieldPair, Comparison comparison) {
        this.fieldPair = fieldPair;
        this.comparison = comparison;
    }

    @Override
    public Boolean getValue(Enchantment enchantment, ItemStack stack, I original) {
        try {
            return comparison.compare(fieldPair.left().get(enchantment, stack, original), fieldPair.right().get(enchantment, stack, original));
        } catch (UnsupportedOperationException ex) {
            if (!hasLoggedError) {
                EnchantmentConfigApi.LOGGER.error("Could not handle {} variable with comparison {}. Returning false.", EnchantmentConfigRegistries.VARIABLE_SERIALIZER.getKey(fieldPair.left().getInnerVariable().getSerializer()), ex);
                hasLoggedError = true;
            }
        }
        return false;
    }

    public boolean allowedInRootCondition() {
        return (fieldPair.left().getInnerVariable() == null || fieldPair.left().getInnerVariable().allowedInRootCondition()) && (fieldPair.right().getInnerVariable() == null || fieldPair.right().getInnerVariable().allowedInRootCondition());
    }

    @Override
    public VariableSerializer<I, Boolean> getSerializer() {
        return (VariableSerializer<I, Boolean>) SERIALIZER;
    }

    public static class Serializer extends VariableSerializer<Object, Boolean> {
        @Override
        public VariableType<Object> inputType(@Nullable VariableType<?> contextType) {
            if (contextType == null)
                return null;
            return (VariableType<Object>) contextType;
        }

        @Override
        public VariableType<Boolean> outputType(@Nullable VariableType<?> contextType) {
            return VariableTypes.BOOLEAN;
        }

        @Override
        public MapCodec<ConditionalVariable<Object, Object>> codec(VariableType<Object> variableType) {
            return staticCodec(variableType);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
