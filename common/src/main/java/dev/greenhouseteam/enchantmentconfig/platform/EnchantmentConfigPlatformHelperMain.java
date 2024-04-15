package dev.greenhouseteam.enchantmentconfig.platform;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigGetterImpl;

public interface EnchantmentConfigPlatformHelperMain extends EnchantmentConfigPlatformHelper {
    @Override
    default EnchantmentConfigGetter createGetter() {
        return new EnchantmentConfigGetterImpl();
    }
}
