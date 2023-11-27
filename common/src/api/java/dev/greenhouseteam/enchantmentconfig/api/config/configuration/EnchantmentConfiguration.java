package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

public interface EnchantmentConfiguration {

    /**
     * Defines how to merge this enchantment configuration into the previous and global
     * configurations.
     *
     * @param oldConfiguration      The original configuration to merge into this config.
     * @param priority              The priority of the current merge.
     * @param oldPriority           The value at which the priority must be higher than to
     *                              have the current value be merged.
     * @param globalPriority        The value at which the priority must be lower than to
     *                              have the global value be merged if it is present.
     *
     * @return                      The merged enchantment configuration.
     */
    EnchantmentConfiguration merge(EnchantmentConfiguration oldConfiguration, int priority, int oldPriority, int globalPriority);

    default EnchantmentConfiguration mergeInternal(EnchantmentConfiguration oldConfiguration, int priority, int oldPriority, int globalPriority) {
        if (!isSameType(oldConfiguration)) {
            throw new ClassCastException("Could not merge enchantment configurations of different types.");
        }
        return this.merge(oldConfiguration, priority, oldPriority, globalPriority);
    }

    default boolean isSameType(EnchantmentConfiguration oldConfiguration) {
        return oldConfiguration.getClass().isAssignableFrom(this.getClass());
    }

}
