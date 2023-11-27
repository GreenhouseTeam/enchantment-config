package dev.greenhouseteam.enchantmentconfig.api.entrypoint;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

// TODO: Document this.
public interface IEnchantmentConfigAssigner {

    <T extends EnchantmentConfiguration> EnchantmentType<T> registerEnchantmentType(EnchantmentType<T> enchantmentType);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType);

}
