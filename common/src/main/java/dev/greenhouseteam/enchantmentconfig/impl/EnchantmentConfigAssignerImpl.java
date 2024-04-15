package dev.greenhouseteam.enchantmentconfig.impl;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentConfigAssignerImpl implements EnchantmentConfigAssigner {
    private static final Map<ResourceKey<EnchantmentType<?>>, EnchantmentType<?>> ENCHANTMENT_TYPE_MAP = new HashMap<>();
    private static final Map<ResourceLocation, MapCodec<? extends EnchantmentVariable<?>>> ENCHANTMENT_VARIABLE_CODEC_MAP = new HashMap<>();

    public <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType) {
        ENCHANTMENT_TYPE_MAP.put(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, enchantmentType.getPath()), enchantmentType);
    }

    @Override
    public void registerVariableCodec(ResourceLocation id, MapCodec<? extends EnchantmentVariable<?>> enchantmentType) {
        ENCHANTMENT_VARIABLE_CODEC_MAP.put(id, enchantmentType);
    }

    @Override
    public void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, ExtraFieldType<?> extraFieldType) {
        if (!EnchantmentConfigRegistries.ENCHANTMENT_TYPE.containsKey(enchantmentType)) {
            throw new NullPointerException("Tried adding field to EnchantmentType '" + enchantmentType.location() + "', which could not be found in the enchantment type registry. You may have also added a field too late.");
        }
        ENCHANTMENT_TYPE_MAP.get(enchantmentType).addExtraFieldType(extraFieldType.key(), extraFieldType);
    }

    protected void registerTypes(RegistrationCallback<EnchantmentType<?>> callback) {
        ENCHANTMENT_TYPE_MAP.forEach((key, type) -> callback.register(EnchantmentConfigRegistries.ENCHANTMENT_TYPE, key.location(), type));
        ENCHANTMENT_TYPE_MAP.clear();
    }

    protected void registerCodecs(RegistrationCallback<MapCodec<? extends EnchantmentVariable<?>>> callback) {
        ENCHANTMENT_VARIABLE_CODEC_MAP.forEach((id, codec) -> callback.register(EnchantmentConfigRegistries.ENCHANTMENT_VARIABLE_CODEC, id, codec));
        ENCHANTMENT_VARIABLE_CODEC_MAP.clear();
    }
}
