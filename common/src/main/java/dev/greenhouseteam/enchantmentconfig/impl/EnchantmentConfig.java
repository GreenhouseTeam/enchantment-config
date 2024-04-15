package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

public class EnchantmentConfig {
    private static EnchantmentConfigPlatformHelper helper;

    public static void init(EnchantmentConfigPlatformHelper helper) {
        EnchantmentConfig.helper = helper;
    }

    public static EnchantmentConfigPlatformHelper getHelper() {
        return helper;
    }

}