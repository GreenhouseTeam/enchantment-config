package dev.greenhouseteam.enchantmentconfig.data;

import com.google.gson.JsonElement;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigFabric;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public class EnchantmentConfigLoaderFabric extends EnchantmentConfigLoader implements IdentifiableResourceReloadListener {

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> map, ResourceManager manager, ProfilerFiller filler) {
        super.apply(map, manager, filler);
        EnchantmentConfigFabric.setRegistryLookup(null);
    }

    @Override
    public ResourceLocation getFabricId() {
        return EnchantmentConfigUtil.asResource("configuration_loader");
    }
}
