package dev.greenhouseteam.enchantmentconfig.platform.services;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.platform.Services;
import net.minecraft.core.Registry;

public interface IPlatformHelper {
    IPlatformHelper INSTANCE = Services.load(IPlatformHelper.class);

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the Enchantment Type registry.
     *
     * @return The enchantment type registry.
     */
    Registry<EnchantmentType<?>> getEnchantmentTypeRegistry();
}