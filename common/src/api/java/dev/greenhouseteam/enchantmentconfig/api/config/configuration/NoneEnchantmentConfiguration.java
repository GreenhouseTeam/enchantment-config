package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.MapCodec;

import java.util.Optional;

public class NoneEnchantmentConfiguration implements EnchantmentConfiguration {
    public static final MapCodec<NoneEnchantmentConfiguration> CODEC = MapCodec.unit(NoneEnchantmentConfiguration::new);

    @Override
    public EnchantmentConfiguration merge(Optional<EnchantmentConfiguration> oldConfiguration) {
        return new NoneEnchantmentConfiguration();
    }
}
