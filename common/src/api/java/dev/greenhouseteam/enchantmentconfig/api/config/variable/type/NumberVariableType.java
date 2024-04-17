package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;

public class NumberVariableType<T extends Number> implements VariableType<T> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("number");

    public Codec<T> getValueCodec() {
        return Codec.DOUBLE.xmap(d -> (T)d, Number::doubleValue);
    }
}
