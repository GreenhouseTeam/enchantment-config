package dev.greenhouseteam.enchantmentconfig.api.config;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;

import java.util.HashMap;
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
                                 Map<String, ?> extraFields) {
        this.type = type;
        this.configuration = configuration;
        this.globalFields = globalFields;
        this.extraFields = (Map<String, Object>) extraFields;
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
        return extraFields;
    }

    /**
     * Retrieves an extra field of this enchantment.
     *
     * @param type                  The ExtraFieldType to get.
     *
     * @return                      Returns the field of the specified name
     *
     * @param <F>                   The type of the castClass field.
     * @throws  ClassCastException  If the value cannot be cast to the class cast field.
     */
    public <F> F getExtraField(ExtraFieldType<F> type) {
        if (!extraFields.containsKey(type.key()))
            return null;

        Object object = extraFields.get(type.key());
        return (F) object;
    }

    public ConfiguredEnchantment<C, T> merge(ConfiguredEnchantment<C, T> oldConfigured, Optional<ConfiguredEnchantment<C, T>> globalConfigured) {
        C configuration = (C) getConfiguration().mergeInternal(oldConfigured.getConfiguration());

        GlobalEnchantmentFields globalFields = getGlobalFields().merge(oldConfigured.getGlobalFields(), globalConfigured.map(ConfiguredEnchantment::getGlobalFields));

        Map<String, Object> extraFields = new HashMap<>();
        for (String key : getExtraFields().keySet()) {
            ExtraFieldType<Object> extraType = (ExtraFieldType<Object>) type.getExtraFieldTypes().get(key);
            Object merged = extraType.merge(getExtraFields().get(key), oldConfigured.getExtraFields().get(key));
            extraFields.put(key, merged);
        }

        return new ConfiguredEnchantment<>(getType(), configuration, globalFields, extraFields);
    }

}
