package dev.greenhouseteam.enchantmentconfig.api.config;

import com.google.common.collect.ImmutableMap;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;

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
        ImmutableMap.Builder<String, Object> remappedExtraFields = ImmutableMap.builder();
        extraFields.forEach(remappedExtraFields::put);
        this.extraFields = remappedExtraFields.build();
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

    public ConfiguredEnchantment<C, T> merge(ConfiguredEnchantment<C, T> oldConfigured, Optional<ConfiguredEnchantment<C, T>> globalConfigured) {
        C configuration = (C) this.getConfiguration().mergeInternal(oldConfigured.getConfiguration());

        GlobalEnchantmentFields globalFields = this.getGlobalFields().merge(oldConfigured.getGlobalFields(), globalConfigured.map(ConfiguredEnchantment::getGlobalFields));

        Map<String, Object> extraFields = new HashMap<>();
        for (String key : getExtraFields().keySet()) {
            ExtraFieldType<Object> extraType = (ExtraFieldType<Object>) type.getExtraFieldTypes().get(key);
            Object merged = extraType.merge(getExtraFields().get(key), oldConfigured.getExtraFields().get(key));
            extraFields.put(key, merged);
        }

        return new ConfiguredEnchantment<>(this.getType(), configuration, globalFields, extraFields);
    }

}
