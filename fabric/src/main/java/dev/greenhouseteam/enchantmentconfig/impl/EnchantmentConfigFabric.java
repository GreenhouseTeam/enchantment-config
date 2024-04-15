package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelperFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

public class EnchantmentConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantmentConfigUtil.init(new EnchantmentConfigPlatformHelperFabric());

        EnchantmentConfigAssignerImpl assigner = new EnchantmentConfigAssignerImpl();
        FabricLoader.getInstance().getEntrypoints("enchantmentconfig", EnchantmentConfigPlugin.class).forEach(entryPoint -> entryPoint.register(assigner));
        assigner.registerTypes(Registry::register);
    }
}
