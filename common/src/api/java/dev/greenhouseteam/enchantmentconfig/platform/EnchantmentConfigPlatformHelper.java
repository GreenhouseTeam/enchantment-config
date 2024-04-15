package dev.greenhouseteam.enchantmentconfig.platform;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public interface EnchantmentConfigPlatformHelper {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    Platform getPlatform();

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
     * Gets the Enchantment Type registry.
     *
     * @return The enchantment type registry.
     */
    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key);

    EnchantmentConfigGetter createGetter();

    RegistryAccess getRegistries();

}