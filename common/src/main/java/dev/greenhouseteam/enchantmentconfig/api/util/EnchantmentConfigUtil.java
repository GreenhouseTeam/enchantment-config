package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.config.EnchantmentConfiguration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class EnchantmentConfigUtil {
    public static final String MOD_ID = "enchantmentconfig";
    public static final String MOD_NAME = "Enchantment Config";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static boolean hasEffectivenessOverride(EnchantmentConfiguration enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().effectivenessOverrides().containsKey(level);
    }

    public static int getEffectivenessOverride(EnchantmentConfiguration enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().effectivenessOverrides().getOrDefault(level, level);
    }

    public static boolean isCompatible(EnchantmentConfiguration enchantment, EnchantmentConfiguration other) {
        return checkCompatibility(enchantment, other) && checkCompatibility(other, enchantment);
    }

    private static boolean checkCompatibility(EnchantmentConfiguration enchantment, EnchantmentConfiguration other) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().incompatibilities().isPresent() ? IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().incompatibilities().get().stream().anyMatch(holders -> holders.contains(BuiltInRegistries.ENCHANTMENT.getHolderOrThrow(other.getEnchantment()))) : Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.get(enchantment.getEnchantment())).isCompatibleWith(Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.get(other.getEnchantment())));
    }

}
