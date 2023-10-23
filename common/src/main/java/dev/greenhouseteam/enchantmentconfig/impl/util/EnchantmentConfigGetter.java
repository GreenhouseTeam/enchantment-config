package dev.greenhouseteam.enchantmentconfig.impl.util;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

@AutoService(IEnchantmentConfigGetter.class)
public class EnchantmentConfigGetter implements IEnchantmentConfigGetter {
    private final Map<ResourceKey<Enchantment>, ConfiguredEnchantment<?, ?>> ENTRIES = Maps.newHashMap();
    @Override
    @SuppressWarnings("unchecked")
    public <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T configuration) {
        return (ConfiguredEnchantment<C, T>) this.ENTRIES.get(configuration.getEnchantment());
    }

    @Override
    public RegistryAccess getRegistryAccess() {
        return EnchantmentConfig.getRegistryAccess();
    }
}
