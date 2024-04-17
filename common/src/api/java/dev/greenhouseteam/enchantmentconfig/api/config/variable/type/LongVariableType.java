package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.resources.ResourceLocation;

public class LongVariableType extends NumberVariableType<Long> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("long");

    public Codec<Long> getValueCodec() {
        return Codec.LONG;
    }
}
