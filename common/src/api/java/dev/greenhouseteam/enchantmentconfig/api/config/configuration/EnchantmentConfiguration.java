package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import org.jetbrains.annotations.ApiStatus;

public interface EnchantmentConfiguration {

    /**
     * Defines how to merge an old enchantment configuration into this configuration.
     *
     * @param oldConfiguration      The old configuration values to merge into this config.
     * @return                      The merged enchantment configuration.
     */
    EnchantmentConfiguration merge(EnchantmentConfiguration oldConfiguration);

    @ApiStatus.Internal
    default EnchantmentConfiguration mergeInternal(EnchantmentConfiguration oldConfiguration) {
        if (!isSameType(oldConfiguration)) {
            throw new ClassCastException("Could not merge enchantment configurations of different types.");
        }
        return this.merge(oldConfiguration);
    }

    @ApiStatus.Internal
    default boolean isSameType(EnchantmentConfiguration oldConfiguration) {
        return oldConfiguration.getClass().isAssignableFrom(this.getClass());
    }

}
