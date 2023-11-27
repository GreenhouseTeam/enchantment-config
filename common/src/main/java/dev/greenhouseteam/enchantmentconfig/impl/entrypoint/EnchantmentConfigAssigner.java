package dev.greenhouseteam.enchantmentconfig.impl.entrypoint;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.entrypoint.IEnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.platform.services.IEnchantmentConfigPlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@AutoService(IEnchantmentConfigAssigner.class)
public class EnchantmentConfigAssigner implements IEnchantmentConfigAssigner {
    @Override
    public <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> Holder<T> registerEnchantmentType(ResourceLocation typeId, T enchantmentType) {
        return IEnchantmentConfigPlatformHelper.INSTANCE.registerEnchantmentType(typeId, enchantmentType);
    }

    @Override
    public void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType) {
        if (!EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY.containsKey(enchantmentType)) {
            throw new NullPointerException("Tried adding field to EnchantmentType '" + enchantmentType.location() + "', which could not be found in the enchantment type registry.");
        }
        EnchantmentConfigRegistries.ENCHANTMENT_TYPE_REGISTRY.getOrThrow(enchantmentType).addExtraFieldType(path, extraFieldType);
    }
}
