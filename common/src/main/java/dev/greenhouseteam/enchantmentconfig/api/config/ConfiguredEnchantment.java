package dev.greenhouseteam.enchantmentconfig.api.config;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
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

    /**
     * Retrieves an extra field of this enchantment.
     *
     * @param key                   The JSON key of the field.
     * @param castClass             The class of the field.
     *
     * @return                      Returns the field of the specified name
     *
     * @param <F>                   The type of the castClass field.
     * @throws  ClassCastException  If the value cannot be cast to the class cast field.
     */
    public <F> F getExtraField(String key, Class<F> castClass) {
        if (!this.extraFields.containsKey(key))
            return null;

        Object object = this.extraFields.get(key);
        if (!object.getClass().isAssignableFrom(castClass)) {
            throw new ClassCastException("Attempted to cast incorrect field type to extra field object '" + key + "'.");
        }
        return (F) object;
    }

    public ConfiguredEnchantment<C, T> merge(ConfiguredEnchantment<C, T> oldConfigured, Optional<ConfiguredEnchantment<C, T>> globalConfigured, int priority, int oldPriority, int globalPriority) {
        int newPriority = Math.max(priority, oldPriority);

        C configuration = (C) this.getConfiguration().mergeInternal(oldConfigured.getConfiguration(), globalConfigured.map(ConfiguredEnchantment::getConfiguration), priority, oldPriority, globalPriority);

        GlobalEnchantmentFields globalFields = this.getGlobalFields().merge(oldConfigured.getGlobalFields(), globalConfigured.map(ConfiguredEnchantment::getGlobalFields), priority, oldPriority, globalPriority);

        Map<String, Object> extraFields = MergeUtil.mergeMap(this.getExtraFields(), oldConfigured.getExtraFields(), globalConfigured.map(ConfiguredEnchantment::getExtraFields), priority, oldPriority, globalPriority);

        return new ConfiguredEnchantment<>(newPriority, this.getType(), configuration, globalFields, extraFields);
    }

}
