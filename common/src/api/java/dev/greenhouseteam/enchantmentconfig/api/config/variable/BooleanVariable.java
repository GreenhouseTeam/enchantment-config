package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.Codec;

public interface BooleanVariable extends EnchantmentVariable<Boolean> {
    @Override
    default Boolean getDefaultComparisonValue() {
        return true;
    }

    @Override
    default Class<Boolean> getInnerClass() {
        return Boolean.class;
    }

    @Override
    default Codec<Boolean> getComparisonValueCodec() {
        return Codec.BOOL;
    }
}
