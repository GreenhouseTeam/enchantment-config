package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.RegistryAccess;

import java.util.ServiceLoader;

public interface IEnchantmentConfigGetter {
    IEnchantmentConfigGetter INSTANCE = load(IEnchantmentConfigGetter.class);

    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T configuration);

    RegistryAccess getRegistryAccess();
    static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        EnchantmentConfigUtil.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
