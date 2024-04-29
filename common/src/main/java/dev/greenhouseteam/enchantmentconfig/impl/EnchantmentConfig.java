package dev.greenhouseteam.enchantmentconfig.impl;

import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public class EnchantmentConfig {
    private static HolderLookup.Provider registries;

    public static void setRegistryLookup(@Nullable HolderLookup.Provider registries) {
        EnchantmentConfig.registries = registries;
    }

    public static HolderLookup.Provider getRegistryLookup() {
        return EnchantmentConfig.registries;
    }
}