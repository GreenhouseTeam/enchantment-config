package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;

public class EnchantmentConfigRegistriesFabric {
    public static Registry<EnchantmentType<?>> ENCHANTMENT_TYPE_REGISTRY = FabricRegistryBuilder.createSimple(EnchantmentConfigRegistries.ENCHANTMENT_TYPE_KEY).buildAndRegister();
}
