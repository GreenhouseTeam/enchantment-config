package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;

public interface VariableType<T> {
    default T getDefaultComparisonValue() {
        return null;
    }

    Codec<T> getValueCodec();
}
