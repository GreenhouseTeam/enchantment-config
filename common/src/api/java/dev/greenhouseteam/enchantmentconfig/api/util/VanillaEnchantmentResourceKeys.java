package dev.greenhouseteam.enchantmentconfig.api.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * This is a helper class for storing the resource keys of vanilla's
 * enchantments. As vanilla does not register their enchantments
 * using resource keys.
 */
public class VanillaEnchantmentResourceKeys {
    /**
     * Gets the {@link ResourceKey} for a specified enchantment.
     *
     * @param path The path of the enchantment to create a key for.
     * @return The resource key for the specified enchantment.
     */
    public static ResourceKey<Enchantment> getResourceKey(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, new ResourceLocation(path));
    }

    public static final ResourceKey<Enchantment> AQUA_AFFINITY = getResourceKey("aqua_affinity");
    public static final ResourceKey<Enchantment> CHANNELING = getResourceKey("channeling");
    public static final ResourceKey<Enchantment> LOYALTY = getResourceKey("loyalty");
    public static final ResourceKey<Enchantment> RIPTIDE = getResourceKey("riptide");
    public static final ResourceKey<Enchantment> DEPTH_STRIDER = getResourceKey("depth_strider");
    public static final ResourceKey<Enchantment> EFFICIENCY = getResourceKey("efficiency");
    public static final ResourceKey<Enchantment> FIRE_ASPECT = getResourceKey("fire_aspect");
    public static final ResourceKey<Enchantment> FLAME = getResourceKey("flame");
    public static final ResourceKey<Enchantment> FORTUNE = getResourceKey("fortune");
    public static final ResourceKey<Enchantment> FROST_WALKER = getResourceKey("frost_walker");
    public static final ResourceKey<Enchantment> IMPALING = getResourceKey("impaling");
    public static final ResourceKey<Enchantment> INFINITY = getResourceKey("infinity");
    public static final ResourceKey<Enchantment> KNOCKBACK = getResourceKey("knockback");
    public static final ResourceKey<Enchantment> LOOTING = getResourceKey("looting");
    public static final ResourceKey<Enchantment> LUCK_OF_THE_SEA = getResourceKey("luck_of_the_sea");
    public static final ResourceKey<Enchantment> LURE = getResourceKey("lure");
    public static final ResourceKey<Enchantment> MENDING = getResourceKey("mending");
    public static final ResourceKey<Enchantment> MULTISHOT = getResourceKey("multishot");
    public static final ResourceKey<Enchantment> POWER = getResourceKey("power");
    public static final ResourceKey<Enchantment> PUNCH = getResourceKey("punch");
    public static final ResourceKey<Enchantment> PROTECTION = getResourceKey("protection");
    public static final ResourceKey<Enchantment> BLAST_PROTECTION = getResourceKey("blast_protection");
    public static final ResourceKey<Enchantment> FIRE_PROTECTION = getResourceKey("fire_protection");
    public static final ResourceKey<Enchantment> FEATHER_FALLING = getResourceKey("feather_falling");
    public static final ResourceKey<Enchantment> PROJECTILE_PROTECTION = getResourceKey("projectile_protection");
    public static final ResourceKey<Enchantment> PIERCING = getResourceKey("piercing");
    public static final ResourceKey<Enchantment> QUICK_CHARGE = getResourceKey("quick_charge");
    public static final ResourceKey<Enchantment> RESPIRATION = getResourceKey("respiration");
    public static final ResourceKey<Enchantment> SOUL_SPEED = getResourceKey("soul_speed");
    public static final ResourceKey<Enchantment> SWIFT_SNEAK = getResourceKey("swift_sneak");
    public static final ResourceKey<Enchantment> SHARPNESS = getResourceKey("sharpness");
    public static final ResourceKey<Enchantment> BANE_OF_ARTHROPODS = getResourceKey("bane_of_arthropods");
    public static final ResourceKey<Enchantment> SMITE = getResourceKey("smite");
    public static final ResourceKey<Enchantment> SWEEPING_EDGE = getResourceKey("sweeping_edge");
    public static final ResourceKey<Enchantment> SILK_TOUCH = getResourceKey("silk_touch");
    public static final ResourceKey<Enchantment> THORNS = getResourceKey("thorns");
    public static final ResourceKey<Enchantment> UNBREAKING = getResourceKey("unbreaking");
    public static final ResourceKey<Enchantment> CURSE_OF_BINDING = getResourceKey("binding_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_VANISHING = getResourceKey("vanishing_curse");
}
