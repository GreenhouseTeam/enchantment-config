package dev.greenhouseteam.enchantmentconfig.impl.util;

import com.google.common.collect.Maps;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public class EnchantmentConfigGetter implements IEnchantmentConfigGetter {
    private Map<ResourceKey<Enchantment>, ConfiguredEnchantment> ENTRIES = Maps.newHashMap();

    @Override
    public <T extends ConfiguredEnchantment> T getConfig(ResourceKey<Enchantment> enchantment) {
        return (T) ENTRIES.get(enchantment);
    }

    @Override
    public RegistryAccess getRegistryAccess() {
        return EnchantmentConfig.getRegistryAccess();
    }
}
