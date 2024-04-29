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
     * Gets a configured enchantment from a {@link ResourceLocation}, or null if one is not specified.
     *
     * @param id    The {@link ResourceLocation} to find an enchantment from.
     *
     * @return      The configured enchantment associated with the {@link ResourceLocation}, or null if one is not present.
     * @param <C>   The configuration of the configured enchantment.
     * @param <T>   The type of the configured enchantment.
     */
    @Nullable
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceLocation id) {
        return getConfig(id, true);
    }

    /**
     * Gets a configured enchantment from a {@link ResourceLocation}.
     *
     * @param id                    The {@link ResourceLocation} to find an enchantment from.
     *
     * @return                      The configured enchantment associated with the {@link ResourceLocation}.
     * @param <C>                   The configuration of the configured enchantment.
     * @param <T>                   The type of the configured enchantment.
     * @throws NullPointerException If the configured enchantment is not present.
     */
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfigOrThrow(ResourceLocation id) {
        return getConfig(id, false);
    }

    /**
     * Gets a configured enchantment from a {@link ResourceKey}.
     *
     * @param key                   The {@link ResourceKey} to find an enchantment from.
     *
     * @return                      The configured enchantment associated with the {@link ResourceKey}.
     * @param <C>                   The configuration of the configured enchantment.
     * @param <T>                   The type of the configured enchantment.
     */
    @Nullable
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceKey<Enchantment> key) {
        return getConfig(key.location(), true);
    }

    /**
     * Gets a configured enchantment from a {@link ResourceKey}.
     *
     * @param key                   The {@link ResourceKey} to find an enchantment from.
     *
     * @return                      The configured enchantment associated with the {@link ResourceKey}.
     * @param <C>                   The configuration of the configured enchantment.
     * @param <T>                   The type of the configured enchantment.
     * @throws NullPointerException If the configured enchantment is not present.
     */
    default <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfigOrThrow(ResourceKey<Enchantment> key) {
        return getConfig(key.location(), false);
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

    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type, boolean nullSafe);

    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(ResourceLocation resourceLocation, boolean nullSafe);

    @Nullable
    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(Enchantment enchantment, boolean nullSafe);

}
