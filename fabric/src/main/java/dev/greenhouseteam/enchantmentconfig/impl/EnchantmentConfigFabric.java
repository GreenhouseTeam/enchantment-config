package dev.greenhouseteam.enchantmentconfig.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EnchantmentConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantmentConfig.init();
        ServerLifecycleEvents.SERVER_STARTING.register(EnchantmentConfig::setServer);
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> EnchantmentConfig.clearServer());
    }
}
