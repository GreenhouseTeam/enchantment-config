package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

// TODO: Document this.
public interface EnchantmentConfigAssigner {

    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> T registerEnchantmentType(T enchantmentType);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType);

}
