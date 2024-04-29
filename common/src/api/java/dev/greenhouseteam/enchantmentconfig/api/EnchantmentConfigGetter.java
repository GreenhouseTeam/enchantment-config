package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentConfigGetter {
    EnchantmentConfigGetter INSTANCE = EnchantmentConfigApi.getHelper().createGetter();

    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type, boolean nullSafe);

    /**
     * Gets a configured enchantment for a specific enchantment type, or null if one has not been loaded.
     *
     * @param type  The type of the configured enchantment to get.
     * @return      Returns a configured enchantment, or null if one is not present.
     *
     * @param <C>   The configuration of the configured enchantment.
     * @param <T>   The type of the configured enchantment.
     */
    @Nullable
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type) {
        return getConfig(type, true);
    }

    /**
     * Gets a configured enchantment for a specific enchantment type.
     *
     * @param type  The type of the configured enchantment to get.
     * @return      Returns a configured enchantment.
     *
     * @param <C>   The configuration of the configured enchantment.
     * @param <T>   The type of the configured enchantment.
     * @throws NullPointerException If the configured enchantment is not present.
     */
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfigOrThrow(T type) {
        return getConfig(type, false);
    }

    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceKey<Enchantment> enchantmentKey, boolean nullSafe) {
        return getConfig(enchantmentKey.location(), nullSafe);
    }
    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceLocation resourceLocation, boolean nullSafe);
    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(Enchantment enchantment, boolean nullSafe);

    /**
     * Gets a configured enchantment for a specified enchantment, or null if one is not specified.
     *
     * @param enchantment   The enchantment to get a configured enchantment from.
     * @return              Returns a configured enchantment, or null if one is not present.
     *
     * @param <C>           The configuration of the configured enchantment.
     * @param <T>           The type of the configured enchantment.
     */
    @Nullable
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(Enchantment enchantment) {
        return getConfig(enchantment, true);
    }

    /**
     * Gets a configured enchantment for a specified enchantment.
     *
     * @param enchantment   The enchantment to get a configured enchantment from.
     * @return              Returns a configured enchantment.
     *
     * @param <C>           The configuration of the configured enchantment.
     * @param <T>           The type of the configured enchantment.
     * @throws NullPointerException If the configured enchantment is not present.
     */
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfigOrThrow(Enchantment enchantment) {
        return getConfig(enchantment, false);
    }

    /**
     * Gets an extra field from the config associated with the
     * specified type and casts it to the proper type.
     *
     * @param key       The resource key associated with the
     *                  config being searched for.
     * @param fieldType The field type to get.
     * @param <T>       The type of the field.
     * @return The field value inside the config,
     * or null if it is not present.
     * @throws ClassCastException When the object cannot be cast to the class.
     */
    @Nullable
    <T> T getExtraField(EnchantmentType<?> key, ExtraFieldType<T> fieldType);
}
