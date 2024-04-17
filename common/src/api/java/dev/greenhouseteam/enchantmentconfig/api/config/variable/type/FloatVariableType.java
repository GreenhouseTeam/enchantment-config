package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;

public class FloatVariableType extends NumberVariableType<Float> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("float");

    public Codec<Float> getValueCodec() {
        return Codec.FLOAT;
    }
}
