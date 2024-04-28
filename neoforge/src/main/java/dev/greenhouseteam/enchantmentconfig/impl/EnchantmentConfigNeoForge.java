package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
            if (EnchantmentConfig.getAndClearModificationType() == ModificationType.NO_CONFIGS)
                return;

            if (event.getTargetEnchant() != null) {
                setEnchantmentLevel(event.getEnchantments(), event.getStack(), event.getTargetEnchant());
                return;
            }

            for (EnchantmentType<?> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE) {
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
                if (enchantment == null)
                    continue;
                setEnchantmentLevel(event.getEnchantments(), event.getStack(), enchantment);
            }
        }

        private static void setEnchantmentLevel(ItemEnchantments.Mutable enchantments, ItemStack stack, Enchantment enchantment) {
            ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment);
            if (configured == null) return;
            int originalLevel = enchantments.getLevel(enchantment);
            enchantments.set(enchantment, configured.getGlobalFields().getOverrideLevel(originalLevel, enchantment, stack));
        }

        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new EnchantmentConfigLoader());
        }
    }
}