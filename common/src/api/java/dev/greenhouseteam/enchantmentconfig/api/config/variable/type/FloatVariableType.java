package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;

public class FloatVariableType extends NumberVariableType<Float> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("float");

    public Codec<Float> getValueCodec() {
        return Codec.FLOAT;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
