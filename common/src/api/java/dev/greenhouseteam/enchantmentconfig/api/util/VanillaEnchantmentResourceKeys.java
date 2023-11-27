package dev.greenhouseteam.enchantmentconfig.api.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * This is a helper class for storing the resource keys of vanilla's
 * enchantments. As vanilla does not register their enchantments
 * using resource keys.
 */
public class VanillaEnchantmentResourceKeys {
    /**
     * Gets the {@link ResourceKey} for a specified enchantment.
     *
     * @param enchantment The enchantment to get the key for.
     * @return            The enchantment's key, or null
     *                    if the enchantment is not present in the
     *                    enchantment registry.
     */
    public static ResourceKey<Enchantment> getResourceKey(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment).orElse(null);
    }

    public static final ResourceKey<Enchantment> AQUA_AFFINITY = getResourceKey(Enchantments.AQUA_AFFINITY);
    public static final ResourceKey<Enchantment> CHANNELING = getResourceKey(Enchantments.CHANNELING);
    public static final ResourceKey<Enchantment> LOYALTY = getResourceKey(Enchantments.LOYALTY);
    public static final ResourceKey<Enchantment> RIPTIDE = getResourceKey(Enchantments.RIPTIDE);
    public static final ResourceKey<Enchantment> DEPTH_STRIDER = getResourceKey(Enchantments.DEPTH_STRIDER);
    public static final ResourceKey<Enchantment> EFFICIENCY = getResourceKey(Enchantments.BLOCK_EFFICIENCY);
    public static final ResourceKey<Enchantment> FIRE_ASPECT = getResourceKey(Enchantments.FIRE_ASPECT);
    public static final ResourceKey<Enchantment> FLAME = getResourceKey(Enchantments.FLAMING_ARROWS);
    public static final ResourceKey<Enchantment> FORTUNE = getResourceKey(Enchantments.BLOCK_FORTUNE);
    public static final ResourceKey<Enchantment> FROST_WALKER = getResourceKey(Enchantments.FROST_WALKER);
    public static final ResourceKey<Enchantment> IMPALING = getResourceKey(Enchantments.IMPALING);
    public static final ResourceKey<Enchantment> INFINITY = getResourceKey(Enchantments.INFINITY_ARROWS);
    public static final ResourceKey<Enchantment> KNOCKBACK = getResourceKey(Enchantments.KNOCKBACK);
    public static final ResourceKey<Enchantment> LOOTING = getResourceKey(Enchantments.MOB_LOOTING);
    public static final ResourceKey<Enchantment> LUCK_OF_THE_SEA = getResourceKey(Enchantments.FISHING_LUCK);
    public static final ResourceKey<Enchantment> LURE = getResourceKey(Enchantments.FISHING_SPEED);
    public static final ResourceKey<Enchantment> MENDING = getResourceKey(Enchantments.MENDING);
    public static final ResourceKey<Enchantment> MULTISHOT = getResourceKey(Enchantments.MULTISHOT);
    public static final ResourceKey<Enchantment> POWER = getResourceKey(Enchantments.POWER_ARROWS);
    public static final ResourceKey<Enchantment> PUNCH = getResourceKey(Enchantments.PUNCH_ARROWS);
    public static final ResourceKey<Enchantment> PROTECTION = getResourceKey(Enchantments.ALL_DAMAGE_PROTECTION);
    public static final ResourceKey<Enchantment> BLAST_PROTECTION = getResourceKey(Enchantments.BLAST_PROTECTION);
    public static final ResourceKey<Enchantment> FIRE_PROTECTION = getResourceKey(Enchantments.FIRE_PROTECTION);
    public static final ResourceKey<Enchantment> FEATHER_FALLING = getResourceKey(Enchantments.FALL_PROTECTION);
    public static final ResourceKey<Enchantment> PROJECTILE_PROTECTION = getResourceKey(Enchantments.PROJECTILE_PROTECTION);
    public static final ResourceKey<Enchantment> PIERCING = getResourceKey(Enchantments.PIERCING);
    public static final ResourceKey<Enchantment> QUICK_CHARGE = getResourceKey(Enchantments.QUICK_CHARGE);
    public static final ResourceKey<Enchantment> RESPIRATION = getResourceKey(Enchantments.RESPIRATION);
    public static final ResourceKey<Enchantment> SOUL_SPEED = getResourceKey(Enchantments.SOUL_SPEED);
    public static final ResourceKey<Enchantment> SWIFT_SNEAK = getResourceKey(Enchantments.SWIFT_SNEAK);
    public static final ResourceKey<Enchantment> SHARPNESS = getResourceKey(Enchantments.SHARPNESS);
    public static final ResourceKey<Enchantment> SMITE = getResourceKey(Enchantments.SMITE);
    public static final ResourceKey<Enchantment> BANE_OF_ARTHROPODS = getResourceKey(Enchantments.BANE_OF_ARTHROPODS);
    public static final ResourceKey<Enchantment> SWEEPING_EDGE = getResourceKey(Enchantments.SWEEPING_EDGE);
    public static final ResourceKey<Enchantment> SILK_TOUCH = getResourceKey(Enchantments.SILK_TOUCH);
    public static final ResourceKey<Enchantment> THORNS = getResourceKey(Enchantments.THORNS);
    public static final ResourceKey<Enchantment> UNBREAKING = getResourceKey(Enchantments.UNBREAKING);
    public static final ResourceKey<Enchantment> CURSE_OF_BINDING = getResourceKey(Enchantments.BINDING_CURSE);
    public static final ResourceKey<Enchantment> CURSE_OF_VANISHING = getResourceKey(Enchantments.VANISHING_CURSE);
}
