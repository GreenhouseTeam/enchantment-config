package dev.greenhouseteam.enchantmentconfig.api.config;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import org.jetbrains.annotations.ApiStatus;

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

    @ApiStatus.Internal
    public ConfiguredEnchantment<C, T> merge(Optional<ConfiguredEnchantment<?, ?>> oldConfigured, Optional<ConfiguredEnchantment<?, ?>> globalConfigured) {
        if (oldConfigured.isPresent() && !getConfiguration().isSameType(oldConfigured.get().getConfiguration()))
            throw new RuntimeException("Attempted to merge config with an old config that is not of the same type.");

        C configuration = (C) getConfiguration().mergeInternal(oldConfigured.map(ConfiguredEnchantment::getConfiguration));

        GlobalEnchantmentFields globalFields = getGlobalFields().merge(oldConfigured.map(ConfiguredEnchantment::getGlobalFields), globalConfigured.map(ConfiguredEnchantment::getGlobalFields));

        Map<String, Object> extraFields = new HashMap<>();
        for (String key : getExtraFields().keySet()) {
            ExtraFieldType<Object> extraType = (ExtraFieldType<Object>) type.getExtraFieldTypes().get(key);
            Object merged = extraType.merge(getExtraFields().get(key), oldConfigured.map(config -> config.getExtraFields().get(key)));
            extraFields.put(key, merged);
        }

        return new ConfiguredEnchantment<>(getType(), configuration, globalFields, extraFields);
    }

}
