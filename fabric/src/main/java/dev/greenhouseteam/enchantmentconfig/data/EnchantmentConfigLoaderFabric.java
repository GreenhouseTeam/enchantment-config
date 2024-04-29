package dev.greenhouseteam.enchantmentconfig.data;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class EnchantmentConfigLoaderFabric extends EnchantmentConfigLoader implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return EnchantmentConfigApi.asResource("configuration_loader");
    }
}
