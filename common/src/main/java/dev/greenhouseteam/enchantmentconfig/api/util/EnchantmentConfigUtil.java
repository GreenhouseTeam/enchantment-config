package dev.greenhouseteam.enchantmentconfig.api.util;

import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class EnchantmentConfigUtil {
    public static final String MOD_ID = "enchantmentconfig";
    public static final String MOD_NAME = "Enchantment Config";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static boolean hasEffectivenessOverride(EnchantmentType<?> enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().effectivenessOverrides().containsKey(level);
    }

    public static int getEffectivenessOverride(EnchantmentType<?> enchantment, int level) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().effectivenessOverrides().getOrDefault(level, level);
    }

    public static boolean isCompatible(EnchantmentType<?> enchantment, EnchantmentType<?> other) {
        return checkCompatibility(enchantment, other) && checkCompatibility(other, enchantment);
    }

    private static boolean checkCompatibility(EnchantmentType<?> enchantment, EnchantmentType<?> other) {
        return IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().incompatibilities().isPresent() ? IEnchantmentConfigGetter.INSTANCE.getConfig(enchantment).getGlobalFields().incompatibilities().get().stream().anyMatch(holders -> holders.contains(BuiltInRegistries.ENCHANTMENT.getHolderOrThrow(other.getEnchantment()))) : Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.get(enchantment.getEnchantment())).isCompatibleWith(Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.get(other.getEnchantment())));
    }

    public static float getFloatFromLevel(int level, float original, Map<Integer, Float> levelToValueMap) {
        if (!levelToValueMap.isEmpty()) {
            if (levelToValueMap.containsKey(level))
                return levelToValueMap.get(level);

            if (levelToValueMap.size() == 1) {
                int key = levelToValueMap.keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Tried to get first damage value when it was not present. This shouldn't happen."));
                return levelToValueMap.get(key) * ((float)level / key);
            }

            int upperBound = levelToValueMap.keySet().stream().filter(i -> i > level).min((o1, o2) -> {
                Integer i1 = Mth.abs(o1 - level);
                Integer i2 = Mth.abs(o2 - level);

                return i1.compareTo(i2);
            }).orElseThrow();
            int lowerBound = levelToValueMap.keySet().stream().filter(i -> i < level).max((o1, o2) -> {
                Integer i1 = Mth.abs(o1 - level);
                Integer i2 = Mth.abs(o2 - level);

                return i1.compareTo(i2);
            }).orElseThrow();

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

}
