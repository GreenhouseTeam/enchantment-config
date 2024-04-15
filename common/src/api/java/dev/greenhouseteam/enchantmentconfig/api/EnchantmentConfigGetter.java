package dev.greenhouseteam.enchantmentconfig.api;

import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigGetterImpl;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentConfigGetter {
    EnchantmentConfigGetter INSTANCE = new EnchantmentConfigGetterImpl();

    <C extends EnchantmentConfiguration, T extends EnchantmentType<C>> ConfiguredEnchantment<C, T> getConfig(T type);

    /**
     * Gets an extra field from the config associated with the
     * specified type and casts it to the proper type.
     * For
     *
     * @param key                   The resource key associated with the
     *                              config being searched for.
     * @param path                  The path to the field in JSON.
     *                              You are able to separate different
     *                              objects through '.'.
     * @param castClass             The class to cast the object to.
     * @return                      The field value inside the config,
     *                              or null if it is not present.
     *
     * @param <T>                   The type of the field.
     * @throws ClassCastException   When the object cannot be cast to the class.
     */
    @Nullable
    <T> T getExtraField(EnchantmentType<?> key, String path, Class<T> castClass);

    RegistryAccess getRegistryAccess();
}
