package dev.greenhouseteam.enchantmentconfig.api.config;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.field.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;

import java.util.Map;
import java.util.Optional;

public class ConfiguredEnchantment<C extends EnchantmentConfiguration, T extends EnchantmentType<C>> {

    private final int priority;
    private final T type;
    private final C configuration;
    private final GlobalEnchantmentFields globalFields;
    private final Map<String, Object> extraFields;

    public ConfiguredEnchantment(int priority,
                                 T type,
                                 C configuration,
                                 GlobalEnchantmentFields globalFields,
                                 Map<String, Object> extraFields) {
        this.priority = priority;
        this.type = type;
        this.configuration = configuration;
        this.globalFields = globalFields;
        this.extraFields = extraFields;
    }

    public int getPriority() {
        return this.priority;
    }

    public T getType() {
        return this.type;
    }

    public C getConfiguration() {
        return this.configuration;
    }

    public GlobalEnchantmentFields getGlobalFields() {
        return this.globalFields;
    }

    public Map<String, Object> getExtraFields() {
        return this.extraFields;
    }

    public <F> F getExtraField(String name, Class<F> castClass) {
        Object object = this.extraFields.get(name);
        if (!object.getClass().isAssignableFrom(castClass)) {
            throw new ClassCastException("Attempted to cast incorrect field type to extra field object '" + name + "'.");
        }
        return (F) object;
    }

    /**
     * Defines how to merge this configured enchantment into the previous one.
     * This also accounts for the global configured enchantments.
     *
     * @param oldConfigured     The original configuration to merge into this config.
     * @param globalConfigured  The global configuration for merging the global value
     *                          into this config. Is not always present and should
     *                          be ignored when so.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be higher than to
     *                          have the current value be merged.
     * @param globalPriority    The value at which the priority must be lower than to
     *                          have the global value be merged if it is present.
     *
     * @return                  A merged EnchantmentConfiguration.
     */
    public ConfiguredEnchantment<C, T> merge(ConfiguredEnchantment<C, T> oldConfigured, Optional<ConfiguredEnchantment<C, T>> globalConfigured, int priority, int oldPriority, int globalPriority) {
        int newPriority = Math.max(priority, oldPriority);

        C configuration = (C) this.getConfiguration().mergeInternal(oldConfigured.getConfiguration(), globalConfigured.map(ConfiguredEnchantment::getConfiguration), priority, oldPriority, globalPriority);

        GlobalEnchantmentFields globalFields = this.getGlobalFields().merge(oldConfigured.getGlobalFields(), globalConfigured.map(ConfiguredEnchantment::getGlobalFields), priority, oldPriority, globalPriority);

        Map<String, Object> extraFields = MergeUtil.mergeMap(this.getExtraFields(), oldConfigured.getExtraFields(), globalConfigured.map(ConfiguredEnchantment::getExtraFields), priority, oldPriority, globalPriority)

        return new ConfiguredEnchantment<>(newPriority, this.getType(), configuration, globalFields, extraFields);
    }

}
