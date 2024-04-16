package dev.greenhouseteam.enchantmentconfig.api.config.variable;

public interface NumberVariable extends EnchantmentVariable<Number> {
    @Override
    default Class<Number> getInnerClass() {
        return Number.class;
    }
}
