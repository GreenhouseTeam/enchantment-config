package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.data.EnchantmentConfigLoaderFabric;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelperFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.PackType;

import javax.annotation.Nullable;

public class EnchantmentConfigFabric implements ModInitializer {
    private static RegistryAccess registries;

    @Override
    public void onInitialize() {
        EnchantmentConfigUtil.init(new EnchantmentConfigPlatformHelperFabric());

        EnchantmentConfigAssignerImpl assigner = new EnchantmentConfigAssignerImpl();
        FabricLoader.getInstance().getEntrypoints("enchantmentconfig", EnchantmentConfigPlugin.class).forEach(entryPoint -> entryPoint.register(assigner));
        assigner.registerTypes(Registry::register);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new EnchantmentConfigLoaderFabric());
    }

    public static void setRegistries(@Nullable RegistryAccess registries) {
        EnchantmentConfigFabric.registries = registries;
    }

    public static RegistryAccess getRegistries() {
        return EnchantmentConfigFabric.registries;
    }
}
