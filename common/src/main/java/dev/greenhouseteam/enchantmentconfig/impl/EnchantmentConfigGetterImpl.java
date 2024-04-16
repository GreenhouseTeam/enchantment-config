package dev.greenhouseteam.enchantmentconfig.impl;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnchantmentConfigGetterImpl implements EnchantmentConfigGetter {
    private static final Map<EnchantmentType<?>, ConfiguredEnchantment<?, ?>> ENTRIES = Maps.newHashMap();

    public void register(ConfiguredEnchantment<?, ?> configuredEnchantment) {
        ENTRIES.put(configuredEnchantment.getType(), configuredEnchantment);
    }

    public void clear() {
        ENTRIES.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type) {
        if (!ENTRIES.containsKey(type))
            throw new NullPointerException("ConfiguredEnchantment for EnchantmentType '" + type.getPath() + "' does not exist at this time or at all.");

        return (ConfiguredEnchantment<C, T>) ENTRIES.get(type);
    }

    @Override
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceLocation resourceLocation, boolean nullSafe) {
        var optional = EnchantmentConfigRegistries.ENCHANTMENT_TYPE.getOptional(resourceLocation);
        if (optional.isEmpty() && !nullSafe)
            throw new NullPointerException("ConfiguredEnchantment for EnchantmentType '" + resourceLocation + "' does not exist at this time or at all.");

        return (ConfiguredEnchantment<C, T>) optional.map(ENTRIES::get).orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(Enchantment enchantment, boolean nullSafe) {
        ResourceLocation enchantmentKey = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        T type = (T) EnchantmentConfigRegistries.ENCHANTMENT_TYPE.get(enchantmentKey);

        if (!ENTRIES.containsKey(type))
            if (!nullSafe) {
                throw new NullPointerException("ConfiguredEnchantment for EnchantmentType '" + enchantmentKey + "' does not exist at this time or at all.");
            } else
                return null;

        return (ConfiguredEnchantment<C, T>) ENTRIES.get(type);
    }

    @Override
    public <T> @Nullable T getExtraField(EnchantmentType<?> key, ExtraFieldType<T> fieldType) {
        return getConfig(key).getExtraField(fieldType);
    }
}
