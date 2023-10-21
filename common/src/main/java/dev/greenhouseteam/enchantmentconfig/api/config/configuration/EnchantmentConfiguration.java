package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import java.util.Optional;

public interface EnchantmentConfiguration {

    /**
     * Defines how to merge this enchantment configuration into the previous and global
     * configurations.
     *
     * @param oldConfiguration      The original configuration to merge into this config.
     * @param globalConfiguration   The global configuration for merging the global value
     *                              into this config. Is not always present and should
     *                              be ignored when so.
     * @param priority              The priority of the current merge.
     * @param oldPriority           The value at which the priority must be higher than to
     *                              have the current value be merged.
     * @param globalPriority        The value at which the priority must be lower than to
     *                              have the global value be merged if it is present.
     *
     * @return                      The merged enchantment configuration.
     */
    EnchantmentConfiguration merge(EnchantmentConfiguration oldConfiguration, Optional<EnchantmentConfiguration> globalConfiguration, int priority, int oldPriority, int globalPriority);

}
