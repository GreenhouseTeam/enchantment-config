package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import net.minecraft.resources.ResourceKey;

// TODO: Document this.
public interface EnchantmentConfigAssigner {
    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType);

    <I, O> void registerVariable(VariableSerializer<I, O> serializer);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, ExtraFieldType<?> extraFieldType);
}
