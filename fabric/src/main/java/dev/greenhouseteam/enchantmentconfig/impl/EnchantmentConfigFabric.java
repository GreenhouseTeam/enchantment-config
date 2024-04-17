package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.data.EnchantmentConfigLoaderFabric;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelperFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;

import javax.annotation.Nullable;

public class EnchantmentConfigFabric implements ModInitializer {
    private static HolderLookup.Provider registries;

    @Override
    public void onInitialize() {
        EnchantmentConfigUtil.init(new EnchantmentConfigPlatformHelperFabric());
        EnchantmentConfig.registerConditionCodecs(Registry::register);
        EnchantmentConfig.registerVariableTypes(Registry::register);

        EnchantmentConfigAssignerImpl assigner = new EnchantmentConfigAssignerImpl();
        FabricLoader.getInstance().getEntrypoints("enchantmentconfig", EnchantmentConfigPlugin.class).forEach(entryPoint -> entryPoint.register(assigner));
        assigner.registerUnregisteredEnchantments();

        assigner.registerTypes(Registry::register);
        assigner.registerCodecs(Registry::register);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new EnchantmentConfigLoaderFabric());
    }

    public static void setRegistryLookup(@Nullable HolderLookup.Provider registries) {
        EnchantmentConfigFabric.registries = registries;
    }

    public static HolderLookup.Provider getRegistryLookup() {
        return EnchantmentConfigFabric.registries;
    }
}
