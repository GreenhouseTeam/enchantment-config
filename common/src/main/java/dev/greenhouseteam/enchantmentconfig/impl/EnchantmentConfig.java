package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

public class EnchantmentConfig {
    private static MinecraftServer server;

    public static void init() {

    }

    public static RegistryAccess getRegistryAccess() {
        return server.registryAccess();
    }

    protected static void setServer(MinecraftServer server) {
        if (EnchantmentConfig.server == null) {
            EnchantmentConfig.server = server;
        } else {
            EnchantmentConfigUtil.LOGGER.warn("Tried setting EnchantmentConfig stored server value while it was not null. This shouldn't happen.");
        }
    }

}