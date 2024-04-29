package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EnchantmentConfigApi {
    public static final String MOD_ID = "enchantmentconfig";
    public static final String MOD_NAME = "Enchantment Config";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final TagKey<Enchantment> DISABLED_ENCHANTMENT_TAG = TagKey.create(Registries.ENCHANTMENT, asResource("disabled"));

    private static ModificationType currentModificationType;

    public static void setModificationType(ModificationType type) {
        currentModificationType = type;
    }

    @Nullable
    public static ModificationType getAndClearModificationType() {
        ModificationType retValue = currentModificationType;
        currentModificationType = null;
        return retValue;
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
        EnchantmentConfigApi.helper = helper;
    }

    public static EnchantmentConfigPlatformHelper getHelper() {
        return helper;
    }
}
