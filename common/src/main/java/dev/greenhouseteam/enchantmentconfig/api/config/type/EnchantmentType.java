package dev.greenhouseteam.enchantmentconfig.api.config.type;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.codec.ExtraFieldsCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.field.GlobalEnchantmentFields;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public class EnchantmentType<T extends EnchantmentConfiguration> {

    private final Codec<T> codec;
    private final ResourceKey<Enchantment> enchantment;

    private final Map<String, ExtraFieldType<Object>> extraFieldTypes = Maps.newHashMap();

    public EnchantmentType(Codec<T> codec, ResourceKey<Enchantment> enchantment) {
        this.codec = codec;
        this.enchantment = enchantment;
    }

    public ResourceKey<Enchantment> getEnchantment() {
        return this.enchantment;
    }

    public Map<String, ExtraFieldType<Object>> getExtraFieldTypes() {
        return this.extraFieldTypes;
    }

    public Codec<ConfiguredEnchantment<T, EnchantmentType<T>>> codec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                EnchantmentConfigCodecs.INT.optionalFieldOf("priority", 0).forGetter(ConfiguredEnchantment::getPriority),
                codec.fieldOf("value").forGetter(ConfiguredEnchantment::getConfiguration),
                GlobalEnchantmentFields.CODEC.forGetter(ConfiguredEnchantment::getGlobalFields),
                new ExtraFieldsCodec(extraFieldTypes).forGetter(ConfiguredEnchantment::getExtraFields)
        ).apply(inst, (t1, t2, t3, t4) -> new ConfiguredEnchantment<>(t1, this, t2, t3, t4)));
    }

}
