package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;

public class DoubleVariableType extends NumberVariableType<Double> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("double");

    public Codec<Double> getValueCodec() {
        return Codec.DOUBLE;
    }
}