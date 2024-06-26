package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;

public class LongVariableType extends NumberVariableType<Long> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("long");

    public Codec<Long> getValueCodec() {
        return Codec.LONG;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
