package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;

public class IntVariableType extends NumberVariableType<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("int");

    public Codec<Integer> getValueCodec() {
        return Codec.INT;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
