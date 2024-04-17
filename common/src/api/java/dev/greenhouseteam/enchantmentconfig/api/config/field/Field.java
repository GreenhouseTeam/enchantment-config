package dev.greenhouseteam.enchantmentconfig.api.config.field;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.NumberVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public class Field<T> {
    private static final Supplier<UnsupportedOperationException> NOT_A_NUMBER_EXCEPTION = () -> new UnsupportedOperationException("Field is not a number.");

    private final Variable<T> variable;
    private final VariableType<T> variableType;
    private final T value;

    public Field(Variable<T> variable) {
        this.variable = variable;
        this.variableType = variable.variableType();
        this.value = null;
    }

    public Field(T value, VariableType<T> type) {
        this.variable = null;
        this.value = value;
        this.variableType = type;
    }

    public VariableType<T> getVariableType() {
        return variableType;
    }

    public T get(Enchantment enchantment, ItemStack stack, T original) {
        if (variable == null)
            return value;
        return variable.getValue(enchantment, stack, original);
    }

    public int getInt(Enchantment enchantment, ItemStack stack, Integer original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.intValue();
        } else if (variableType instanceof NumberVariableType<?>)
            return ((Number)variable.getValue(enchantment, stack, (T) original)).intValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public float getFloat(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.floatValue();
        } else if (variableType instanceof NumberVariableType<?>)
            return ((Number)variable.getValue(enchantment, stack, (T) original)).floatValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public double getDouble(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.doubleValue();
        } else if (variableType instanceof NumberVariableType<?>)
                return ((Number)variable.getValue(enchantment, stack, (T) original)).doubleValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public float getLong(Enchantment enchantment, ItemStack stack, Float original) {
        if (variable == null) {
            if (value instanceof Number number)
                return number.longValue();
        } else if (variableType instanceof NumberVariableType<?>)
                return ((Number)variable.getValue(enchantment, stack, (T) original)).longValue();
        throw NOT_A_NUMBER_EXCEPTION.get();
    }

    public Variable<T> getInnerVariable() {
        return variable;
    }

    public T getRawValue() {
        return value;
    }
}
