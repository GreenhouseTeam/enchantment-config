package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(EnchantmentConfigUtil.MOD_ID)
public class EnchantmentConfigNeo {
    protected static final List<EnchantmentType<?>> ENCHANTMENT_TYPE_LIST = new ArrayList<>();
    public static boolean registered;

    public EnchantmentConfigNeo(IEventBus eventBus) {
        EnchantmentConfigRegistriesNeo.ENCHANTMENT_TYPE_DEFERRED_REGISTER.register(eventBus);
    }

    public static void addEnchantmentTypeToRegistryList(EnchantmentType<?> type) {
        if (registered) {
            throw new UnsupportedOperationException("Could not register EnchantmentTypes for EnchantmentConfig.");
        }
        ENCHANTMENT_TYPE_LIST.add(type);
    }

    @Mod.EventBusSubscriber(modid = EnchantmentConfigUtil.MOD_ID)
    public static class EnchantmentConfigNeoEvents {
        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            EnchantmentConfig.setServer(event.getServer());
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            EnchantmentConfig.clearServer();
        }
    }

}