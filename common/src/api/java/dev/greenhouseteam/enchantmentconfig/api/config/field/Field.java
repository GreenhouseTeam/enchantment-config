package dev.greenhouseteam.enchantmentconfig.api.config.field;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.NumberVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public class Field<I, O> {
    private static final Supplier<UnsupportedOperationException> NOT_A_NUMBER_EXCEPTION = () -> new UnsupportedOperationException("Field is not a number.");

    private final Variable<I, O> variable;
    private final VariableType<I> inputType;
    private final VariableType<O> outputType;
    private final O value;

    public Field(Variable<I, O> variable, VariableType<I> inputType) {
        this.variable = variable;
        this.inputType = variable.getSerializer().inputType(inputType);
        this.outputType = variable.getSerializer().outputType(inputType);
        this.value = null;
    }

    public Field(O value, VariableType<O> type) {
        this.variable = null;
        this.value = value;
        this.inputType = (VariableType<I>) type;
        this.outputType = type;
    }

    public VariableType<I> getInputType() {
        return inputType;
    }

    public VariableType<O> getOutputType() {
        return outputType;
    }

    public O get(Enchantment enchantment, ItemStack stack, I original) {
        if (variable == null)
            return value;
        return variable.getValue(enchantment, stack, original);
    }

    public int getInt(Enchantment enchantment, ItemStack stack, Integer original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.intValue();
        } else if (outputType instanceof NumberVariableType<?>)
            return ((Number)variable.getValue(enchantment, stack, (I) original)).intValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public float getFloat(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.floatValue();
        } else if (outputType instanceof NumberVariableType<?>)
            return ((Number)variable.getValue(enchantment, stack, (I) original)).floatValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public double getDouble(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.doubleValue();
        } else if (outputType instanceof NumberVariableType<?>)
                return ((Number)variable.getValue(enchantment, stack, (I) original)).doubleValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public float getLong(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.longValue();
        } else if (outputType instanceof NumberVariableType<?>)
                return ((Number)variable.getValue(enchantment, stack, (I) original)).longValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public Variable<I, O> getInnerVariable() {
        return variable;
    }

    public O getRawValue() {
        return value;
    }
}
