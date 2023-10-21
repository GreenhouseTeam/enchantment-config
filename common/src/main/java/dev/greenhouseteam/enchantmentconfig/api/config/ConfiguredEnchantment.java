package dev.greenhouseteam.enchantmentconfig.api.config;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.field.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;

import java.util.Map;
import java.util.Optional;

public class ConfiguredEnchantment<C extends EnchantmentConfiguration, T extends EnchantmentType<C>> {

    private final T type;
    private final C configuration;
    private final GlobalEnchantmentFields globalFields;
    private final Map<String, Object> extraFields;

    public ConfiguredEnchantment(T type,
                                 C configuration,
                                 GlobalEnchantmentFields globalFields,
                                 Map<String, Object> extraFields) {
        this.type = type;
        this.configuration = configuration;
        this.globalFields = globalFields;
        this.extraFields = extraFields;
    }

    public T getType() {
        return type;
    }

    public C getConfiguration() {
        return configuration;
    }

    public GlobalEnchantmentFields getGlobalFields() {
        return globalFields;
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
        C configuration = (C) this.getConfiguration().merge(oldConfigured.getConfiguration(), globalConfigured.map(ConfiguredEnchantment::getConfiguration), priority, oldPriority, globalPriority);

        GlobalEnchantmentFields globalFields = this.getGlobalFields().merge(oldConfigured.getGlobalFields(), globalConfigured.map(ConfiguredEnchantment::getGlobalFields), priority, oldPriority, globalPriority);

        Map<String, Object> extraFields = Maps.newHashMap();
        Map<String, ExtraFieldType<Object>> extraFieldTypes = this.getType().getExtraFieldTypes();
        this.getExtraFields().forEach((name, o) -> {
            Object newObject = extraFieldTypes.get(name).merge(o, oldConfigured.getExtraFields().get(name), globalConfigured.map(c -> c.getExtraFields().get(name)), priority, oldPriority, globalPriority);
            extraFields.put(name, newObject);
        });

        return new ConfiguredEnchantment<>(this.getType(), configuration, globalFields, extraFields);
    }

}
