package dev.greenhouseteam.enchantmentconfig.platform;

import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigFabric;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public class EnchantmentConfigPlatformHelperFabric implements EnchantmentConfigPlatformHelperMain {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }

    @Override
    public RegistryAccess getRegistries() {
        return EnchantmentConfigFabric.getRegistries();
    }
}
