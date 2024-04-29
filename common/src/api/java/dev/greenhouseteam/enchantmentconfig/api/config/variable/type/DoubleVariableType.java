package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;

public class DoubleVariableType extends NumberVariableType<Double> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("double");

    public Codec<Double> getValueCodec() {
        return Codec.DOUBLE;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
