package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.data.EnchantmentConfigLoaderFabric;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelperFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;

import java.util.Optional;

public class EnchantmentConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantmentConfigUtil.init(new EnchantmentConfigPlatformHelperFabric());
        EnchantmentConfig.registerConditionCodecs(Registry::register);
        EnchantmentConfig.registerVariableTypes(Registry::register);

        EnchantmentConfigAssignerImpl assigner = new EnchantmentConfigAssignerImpl();
        FabricLoader.getInstance().getEntrypoints("enchantmentconfig", EnchantmentConfigPlugin.class).forEach(entryPoint -> entryPoint.register(assigner));
        assigner.registerUnregisteredEnchantments();

        assigner.registerTypes(Registry::register);
        assigner.registerSerializers(Registry::register);
        registerEvents();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new EnchantmentConfigLoaderFabric());
    }

    public static void registerEvents() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) -> {
            if (enchantingContext != EnchantingContext.RANDOM_ENCHANTMENT) {
                ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment);
                if (configured == null)
                    return TriState.DEFAULT;

                Optional<Boolean> appliable = configured.getGlobalFields().isApplicable(target);
                return appliable.isEmpty() ? TriState.DEFAULT : TriState.of(appliable.get());
            }
            return TriState.DEFAULT;
        });
    }
}
