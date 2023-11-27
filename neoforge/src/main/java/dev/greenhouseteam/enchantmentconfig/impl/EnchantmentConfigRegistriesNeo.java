package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EnchantmentConfigRegistriesNeo {
    public static final DeferredRegister<EnchantmentType<?>> ENCHANTMENT_TYPE_DEFERRED_REGISTER = DeferredRegister.create(EnchantmentConfigRegistries.ENCHANTMENT_TYPE_KEY, EnchantmentConfigUtil.MOD_ID);
    public static final Registry<EnchantmentType<?>> ENCHANTMENT_TYPE_REGISTRY = ENCHANTMENT_TYPE_DEFERRED_REGISTER.makeRegistry(RegistryBuilder::create);
}
