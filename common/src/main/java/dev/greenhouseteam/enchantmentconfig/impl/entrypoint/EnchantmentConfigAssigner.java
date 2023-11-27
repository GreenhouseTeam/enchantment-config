package dev.greenhouseteam.enchantmentconfig.impl.entrypoint;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.entrypoint.IEnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

@AutoService(IEnchantmentConfigAssigner.class)
public class EnchantmentConfigAssigner implements IEnchantmentConfigAssigner {
    @Override
    public <T extends EnchantmentConfiguration> EnchantmentType<T> registerEnchantmentType(EnchantmentType<T> enchantmentType) {
        return Registry.register(EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY, enchantmentType.getPath(), enchantmentType);
    }

    @Override
    public void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType) {
        if (!EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY.containsKey(enchantmentType)) {
            throw new NullPointerException("Tried adding field to EnchantmentType '" + enchantmentType.location() + "', which could not be found in the enchantment type registry.");
        }
        EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY.get(enchantmentType).addExtraFieldType(path, extraFieldType);
    }
}
