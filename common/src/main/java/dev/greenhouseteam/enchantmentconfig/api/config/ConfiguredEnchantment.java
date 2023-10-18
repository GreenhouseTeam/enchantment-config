package dev.greenhouseteam.enchantmentconfig.api.config;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.field.GlobalEnchantmentFields;

import java.util.Map;
import java.util.Optional;

public class ConfiguredEnchantment<T extends EnchantmentConfiguration> {

    private T type;
    private GlobalEnchantmentFields globalFields;

    private Map<String, Object> extraFields;

    public ConfiguredEnchantment(T type,
                                 GlobalEnchantmentFields globalFields,
                                 Map<String, Object> extraFields) {
        this.type = type;
        this.globalFields = globalFields;
        this.extraFields = extraFields;
    }

    public T getType() {
        return type;
    }

    public GlobalEnchantmentFields getGlobalFields() {
        return globalFields;
    }

    public Map<String, Object> getExtraFields() {
        return this.extraFields;
    }

    /**
     * Defines how to merge this enchantment configuration into the previous one.
     * This also accounts for the global configuration.
     *
     * @param oldConfiguration      The original configuration to merge into this config.
     * @param globalConfiguration   The global configuration for merging the global value
     *                              into this config. Is not always present and should
     *                              be ignored when so.
     * @param priority              The priority of the current merge.
     * @param oldPriority           The value at which the priority must be higher than to
     *                              have the current value be merged if it is present.
     * @param globalPriority        The value at which the priority must be lower than to
     *                              have the global value be merged if it is present.
     *
     * @return                      A merged EnchantmentConfiguration.
     */
    public ConfiguredEnchantment<T> merge(ConfiguredEnchantment<T> oldConfiguration, Optional<ConfiguredEnchantment<T>> globalConfiguration, int priority, int oldPriority, int globalPriority) {

        T type = this.getType().merge(oldConfiguration.getType(), globalConfiguration.map(ConfiguredEnchantment::getType), priority, oldPriority, globalPriority);

        GlobalEnchantmentFields globalFields = this.getGlobalFields().merge(oldConfiguration.getGlobalFields(), globalConfiguration.map(ConfiguredEnchantment::getGlobalFields), priority, oldPriority, globalPriority);

        Map<String, Object> extraFields = Maps.newHashMap();
        Map<String, ExtraFieldType<Object>> extraFieldTypes = this.getType().getExtraFieldTypes();
        this.getExtraFields().forEach((name, o) -> {
            Object newObject = extraFieldTypes.get(name).merge(o, oldConfiguration.getExtraFields().get(name), globalConfiguration.map(c -> c.getExtraFields().get(name)), priority, oldPriority, globalPriority);
            extraFields.put(name, newObject);
        });

        return new ConfiguredEnchantment<>(type, globalFields, extraFields);

    }

}
