package dev.greenhouseteam.enchantmentconfig.api.config.field;

import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class VariableField<T> {
    private final EnchantmentVariable<T> variable;
    private final T value;

    public VariableField(EnchantmentVariable<T> variable) {
        this.variable = variable;
        this.value = null;
    }

    public VariableField(T value) {
        this.variable = null;
        this.value = value;
    }

    public T get(Enchantment enchantment, ItemStack stack) {
        if (variable == null)
            return value;
        return variable.getValue(enchantment, stack);
    }

    public EnchantmentVariable<T> getInnerVariable() {
        return variable;
    }

    public T getRawValue() {
        return value;
    }
}
