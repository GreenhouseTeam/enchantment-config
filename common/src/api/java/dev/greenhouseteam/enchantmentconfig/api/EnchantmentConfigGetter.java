package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentConfigGetter {
    EnchantmentConfigGetter INSTANCE = EnchantmentConfigUtil.getHelper().createGetter();

    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type);

    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(Enchantment enchantment, boolean nullSafe);


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
