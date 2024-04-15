package dev.greenhouseteam.enchantmentconfig.impl;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnchantmentConfigGetterImpl implements EnchantmentConfigGetter {
    private static final Map<EnchantmentType<?>, ConfiguredEnchantment<?, ?>> ENTRIES = Maps.newHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type) {
        if (!ENTRIES.containsKey(type))
            throw new NullPointerException("ConfiguredEnchantment for EnchantmentType '" + type.getPath() + "' does not exist at this time or at all.");

        return (ConfiguredEnchantment<C, T>) ENTRIES.get(type);
    }

    @Override
    public <T> @Nullable T getExtraField(EnchantmentType<?> key, String path, Class<T> castClass) {
        return getConfig(key).getExtraField(path, castClass);
    }
}
