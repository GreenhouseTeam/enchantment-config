package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@FunctionalInterface
public interface VariableCodecFunction {
    MapCodec<? extends Variable<?>> create(VariableType<?> type);
}
