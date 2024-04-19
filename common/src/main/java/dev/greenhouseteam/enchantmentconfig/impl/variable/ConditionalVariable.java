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
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ConditionalVariable<I, O> implements Variable<I, Boolean> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("conditional");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<ConditionalVariable<Object, Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            new FieldPair.Codec("value", "compare_to").forGetter(variable -> variable.fieldPair),
            Comparison.CODEC.optionalFieldOf("comparison", Comparison.EQUAL).forGetter(variable -> variable.comparison)
    ).apply(inst, ConditionalVariable::new));

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
                EnchantmentConfigUtil.LOGGER.error("Could not handle {} variable with comparison {}. Returning false.", EnchantmentConfigRegistries.VARIABLE_SERIALIZER.getKey(fieldPair.left().getInnerVariable().getSerializer()), ex);
                hasLoggedError = true;
            }
        }
        return false;
    }

    public boolean allowedInRootCondition() {
        return false;
    }

    @Override
    public VariableSerializer<I, Boolean> getSerializer() {
        return (VariableSerializer<I, Boolean>) SERIALIZER;
    }

    public static class Serializer extends VariableSerializer<Object, Boolean> {
        @Override
        public VariableType<Object> inputType(VariableType<?> type) {
            return (VariableType<Object>) type;
        }

        @Override
        public VariableType<Boolean> outputType(VariableType<?> type) {
            return VariableTypes.BOOLEAN;
        }

        @Override
        public MapCodec<ConditionalVariable<Object, Object>> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
