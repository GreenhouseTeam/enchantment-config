package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ServiceLoader;

public interface IEnchantmentConfigGetter {
    IEnchantmentConfigGetter INSTANCE = load(IEnchantmentConfigGetter.class);
    <T extends ConfiguredEnchantment> T getConfig(ResourceKey<Enchantment> enchantment);

    RegistryAccess getRegistryAccess();
    static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        EnchantmentConfigUtil.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
