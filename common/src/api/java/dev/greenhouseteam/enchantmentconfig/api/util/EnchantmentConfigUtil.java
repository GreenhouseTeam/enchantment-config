package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class EnchantmentConfigUtil {
    public static final String MOD_ID = "enchantmentconfig";
    public static final String MOD_NAME = "Enchantment Config";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Optional<Boolean> isCompatible(Enchantment enchantment, Enchantment other) {
        Optional<Boolean> optional = checkCompatibility(enchantment, other);
        Optional<Boolean> otherOptional = checkCompatibility(other, enchantment);
        return optional.isPresent() && otherOptional.isPresent() ? Optional.of(optional.get() && otherOptional.get()) : optional.isPresent() ? optional : otherOptional;
    }

    private static Optional<Boolean> checkCompatibility(Enchantment enchantment, Enchantment other) {
        ConfiguredEnchantment<?, ?> type = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment, true);
        if (type == null)
            return Optional.empty();

        return EnchantmentConfigGetter.INSTANCE.getConfig(type.getType()).getGlobalFields().incompatibilities().isPresent() ? Optional.of(EnchantmentConfigGetter.INSTANCE.getConfig(type.getType()).getGlobalFields().incompatibilities().get().stream().noneMatch(holders -> holders.contains(other.builtInRegistryHolder()))) : Optional.empty();
    }

    public static int getOverrideLevel(int level, Enchantment enchantment, ItemStack stack, Map<Integer, Field<Integer>> levelToValueMap) {
        if (!levelToValueMap.isEmpty()) {
            if (levelToValueMap.containsKey(level))
                return levelToValueMap.get(level).get(enchantment, stack, level);

            if (levelToValueMap.keySet().stream().allMatch(i -> i > level))
                return level;

            if (levelToValueMap.keySet().stream().allMatch(i -> i < level)) {
                int value = levelToValueMap.keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();
                return levelToValueMap.getOrDefault(value, new Field<>(level)).get(enchantment, stack, level);
            }

            int upperBound = levelToValueMap.keySet().stream().filter(i -> i > level).min(Integer::compareTo).orElseThrow();
            int lowerBound = levelToValueMap.keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();

            int upperLevel = levelToValueMap.getOrDefault(upperBound, new Field<>(level)).get(enchantment, stack, level);
            int lowerLevel = levelToValueMap.getOrDefault(lowerBound, new Field<>(level)).get(enchantment, stack, level);

            if (lowerLevel == upperLevel) {
                return lowerLevel;
            }

            return (int)Mth.lerp(lowerLevel, upperLevel, (float) (level - lowerBound) / (upperBound - lowerBound));
        }

        return level;
    }

    public static float getFloatFromLevel(int level, float original, Map<Integer, Float> levelToValueMap) {
        if (!levelToValueMap.isEmpty()) {
            if (levelToValueMap.containsKey(level))
                return levelToValueMap.get(level);

            if (levelToValueMap.keySet().stream().allMatch(i -> i > level))
                return level;

            if (levelToValueMap.keySet().stream().allMatch(i -> i < level)) {
                int value = levelToValueMap.keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();
                return levelToValueMap.getOrDefault(value, original);
            }

            int upperBound = levelToValueMap.keySet().stream().filter(i -> i > level).min(Integer::compareTo).orElseThrow();
            int lowerBound = levelToValueMap.keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();

            float upperDamage = levelToValueMap.get(upperBound);
            float lowerDamage = levelToValueMap.get(lowerBound);

            if (lowerDamage == upperDamage) {
                return lowerDamage;
            }

            return Mth.lerp(lowerDamage, upperDamage, (float) (level - lowerBound) / (upperBound - lowerBound));
        }

        return original;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static EnchantmentConfigPlatformHelper helper;

    public static void init(EnchantmentConfigPlatformHelper helper) {
        EnchantmentConfigUtil.helper = helper;
    }

    public static EnchantmentConfigPlatformHelper getHelper() {
        return helper;
    }
}
