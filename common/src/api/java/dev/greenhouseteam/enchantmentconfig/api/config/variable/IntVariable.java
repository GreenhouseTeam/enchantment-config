package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.Codec;

public interface IntVariable extends NumberVariable {
    @Override
    default Codec<Number> getComparisonValueCodec() {
        return Codec.INT.xmap(integer -> integer, Number::intValue);
    }
}
