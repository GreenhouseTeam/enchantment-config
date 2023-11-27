package dev.greenhouseteam.enchantmentconfig.platform;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigRegistriesFabric;
import dev.greenhouseteam.enchantmentconfig.platform.services.IEnchantmentConfigPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class FabricEnchantmentConfigPlatformHelper implements IEnchantmentConfigPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
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
    public Registry<EnchantmentType<?>> getEnchantmentTypeRegistry() {
        return EnchantmentConfigRegistriesFabric.ENCHANTMENT_TYPE_REGISTRY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EnchantmentType<?>> Holder<T> registerEnchantmentType(ResourceLocation typeId, T enchantmentType) {
        return (Holder<T>) Registry.registerForHolder(EnchantmentConfigRegistriesFabric.ENCHANTMENT_TYPE_REGISTRY, typeId, enchantmentType);
    }
}
