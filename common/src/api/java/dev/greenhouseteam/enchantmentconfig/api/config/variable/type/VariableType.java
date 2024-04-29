package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface VariableType<T> {
    default T getDefaultComparisonValue() {
        return null;
    }

    Codec<T> getValueCodec();

    ResourceLocation id();
}
