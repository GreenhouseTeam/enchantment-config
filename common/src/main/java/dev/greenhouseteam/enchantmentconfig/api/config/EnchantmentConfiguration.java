package dev.greenhouseteam.enchantmentconfig.api.config;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.ExtraFieldsCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.field.GlobalEnchantmentFields;

import java.util.Map;
import java.util.Optional;

public class EnchantmentConfiguration {

    private Codec<EnchantmentConfiguration> codec;

    private Map<String, ExtraFieldType<Object>> extraFieldTypes = Maps.newHashMap();

    public EnchantmentConfiguration(Codec<EnchantmentConfiguration> codec) {
        this.codec = codec;
    }

    public Map<String, ExtraFieldType<Object>> getExtraFieldTypes() {
        return this.extraFieldTypes;
    }

    public Codec<ConfiguredEnchantment<?>> codec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                codec.fieldOf("value").forGetter(ConfiguredEnchantment::getType),
                GlobalEnchantmentFields.CODEC.forGetter(ConfiguredEnchantment::getGlobalFields),
                new ExtraFieldsCodec(extraFieldTypes).forGetter(ConfiguredEnchantment::getExtraFields)
        ).apply(inst, ConfiguredEnchantment::new));
    }


    // TODO: Document this.
    /**
     *
     * @param oldConfiguration
     * @param globalConfiguration
     * @param priority
     * @param oldPriority
     * @param globalPriority
     *
     * @return                                  A merged enchantment type.
     * @param <T>                               This EnchantmentConfiguration.
     *                                          Unexpected behavior may arise if it is
     *                                          not this one.
     */
    @SuppressWarnings("unchecked")
    public <T extends EnchantmentConfiguration> T merge(T oldConfiguration, Optional<T> globalConfiguration, int priority, int oldPriority, int globalPriority) {
        return (T) this;
    }
}
