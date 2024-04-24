package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@Mod(EnchantmentConfigUtil.MOD_ID)
public class EnchantmentConfigNeoForge {
    public EnchantmentConfigNeoForge(IEventBus eventBus) {

    }

    @EventBusSubscriber(modid = EnchantmentConfigUtil.MOD_ID)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void modifyEnchantmentLevels(GetEnchantmentLevelEvent event) {
            if (EnchantmentConfig.getAndClearModificationType() == ModificationType.NO_CONFIGS && event.getTargetEnchant() != null)
                return;

            for (EnchantmentType<?> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE) {
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
                if (enchantment == null)
                    continue;
                ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(type);
                if (configured == null)
                    continue;
                int originalLevel = event.getEnchantments().getLevel(enchantment);
                event.getEnchantments().set(enchantment, configured.getGlobalFields().getOverrideLevel(originalLevel, enchantment, event.getStack()));
            }
        }

        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new EnchantmentConfigLoader());
        }
    }
}