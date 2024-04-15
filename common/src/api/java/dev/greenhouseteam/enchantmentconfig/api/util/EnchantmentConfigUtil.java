package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.field.VariableField;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
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

    public static int getOverrideLevel(int level, Enchantment enchantment, ItemStack stack, Map<Integer, VariableField<Integer>> levelToValueMap) {
        if (!levelToValueMap.isEmpty()) {
            if (levelToValueMap.containsKey(level))
                return levelToValueMap.get(level).get(enchantment, stack);

            if (levelToValueMap.size() == 1) {
                int key = levelToValueMap.keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Tried to get first damage value when it was not present. This shouldn't happen."));
                return (int) (levelToValueMap.get(key).get(enchantment, stack) * ((float)level / key));
            }

            int upperBound = levelToValueMap.keySet().stream().filter(i -> i > level).min(Integer::compareTo).orElseThrow();
            int lowerBound = levelToValueMap.keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();

            int upperDamage = levelToValueMap.get(upperBound).get(enchantment, stack);
            int lowerDamage = levelToValueMap.get(lowerBound).get(enchantment, stack);

            if (lowerDamage == upperDamage) {
                return lowerDamage;
            }

            return (int)Mth.lerp(lowerDamage, upperDamage, (float) (level - lowerBound) / (upperBound - lowerBound));
        }

        return level;
    }

    public static float getFloatFromLevel(int level, float original, Map<Integer, Float> levelToValueMap) {
        if (!levelToValueMap.isEmpty()) {
            if (levelToValueMap.containsKey(level))
                return levelToValueMap.get(level);

            if (levelToValueMap.size() == 1) {
                int key = levelToValueMap.keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Tried to get first damage value when it was not present. This shouldn't happen."));
                return levelToValueMap.get(key) * ((float)level / key);
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
