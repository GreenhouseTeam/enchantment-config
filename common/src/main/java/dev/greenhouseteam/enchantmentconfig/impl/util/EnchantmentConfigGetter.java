package dev.greenhouseteam.enchantmentconfig.impl.util;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@AutoService(IEnchantmentConfigGetter.class)
public class EnchantmentConfigGetter implements IEnchantmentConfigGetter {
    private final Map<EnchantmentType<?>, ConfiguredEnchantment<?, ?>> ENTRIES = Maps.newHashMap();
    @Override
    @SuppressWarnings("unchecked")
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type) {
        if (!this.ENTRIES.containsKey(type))
            throw new NullPointerException("ConfiguredEnchantment for EnchantmentType '" + type.getPath() + "' does not exist at this time or at all.");

        return (ConfiguredEnchantment<C, T>) this.ENTRIES.get(type);
    }

    @Override
    public <T> @Nullable T getExtraField(EnchantmentType<?> key, String path, Class<T> castClass) {
        return getConfig(key).getExtraField(path, castClass);
    }

    @Override
    public RegistryAccess getRegistryAccess() {
        return EnchantmentConfig.getRegistryAccess();
    }
}
