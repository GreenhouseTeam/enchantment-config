package dev.greenhouseteam.enchantmentconfig.data;

import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class EnchantmentConfigLoaderFabric extends EnchantmentConfigLoader implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return EnchantmentConfigUtil.asResource("configuration_loader");
    }
}
