package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.util.IEnchantmentConfigGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentConfigUtil {
    public static final String MOD_ID = "enchantmentconfig";
    public static final String MOD_NAME = "Enchantment Config";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static boolean hasEffectivenessOverride(ResourceKey<Enchantment> enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getEffectivenessOverrides().containsKey(level);
    }

    public static int getEffectivenessOverride(ResourceKey<Enchantment> enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getEffectivenessOverrides().getOrDefault(level, level);
    }

    public static boolean isCompatible(ResourceKey<Enchantment> enchantment, ResourceKey<Enchantment> other) {
        return checkCompatibility(enchantment, other) && checkCompatibility(other, enchantment);
    }

    private static boolean checkCompatibility(ResourceKey<Enchantment> enchantment, ResourceKey<Enchantment> other) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getIncompatibilities().isPresent() ? IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getIncompatibilities().get().contains(BuiltInRegistries.ENCHANTMENT.getHolderOrThrow(other)) : BuiltInRegistries.ENCHANTMENT.get(enchantment).isCompatibleWith(BuiltInRegistries.ENCHANTMENT.get(other));
    }
}
