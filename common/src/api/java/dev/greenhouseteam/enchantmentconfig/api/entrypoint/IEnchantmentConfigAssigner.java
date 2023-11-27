package dev.greenhouseteam.enchantmentconfig.api.entrypoint;

import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

// TODO: Document this.
public interface IEnchantmentConfigAssigner {

    default <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> Holder<T> registerEnchantmentType(T enchantmentType) {
        return registerEnchantmentType(enchantmentType.getPath(), enchantmentType);
    }

    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> Holder<T> registerEnchantmentType(ResourceLocation typeId, T enchantmentType);

    void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, String path, ExtraFieldType<?> extraFieldType);

}
