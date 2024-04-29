package dev.greenhouseteam.enchantmentconfig.api.config.variable.type;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;

public class BooleanVariableType implements VariableType<Boolean> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("boolean");

    public Boolean getDefaultComparisonValue() {
        return true;
    }

    public Codec<Boolean> getValueCodec() {
        return Codec.BOOL;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
