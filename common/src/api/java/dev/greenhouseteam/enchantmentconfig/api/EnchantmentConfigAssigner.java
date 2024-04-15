package dev.greenhouseteam.enchantmentconfig.api;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

// TODO: Document this.
public interface EnchantmentConfigAssigner {
    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType);

    void registerVariableCodec(ResourceLocation id, MapCodec<? extends EnchantmentVariable<?>> enchantmentType);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, ExtraFieldType<?> extraFieldType);
}
