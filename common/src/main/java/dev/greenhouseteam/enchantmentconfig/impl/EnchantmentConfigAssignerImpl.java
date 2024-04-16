package dev.greenhouseteam.enchantmentconfig.impl;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.NoneEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.EnchantmentVariable;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    protected void registerUnregisteredEnchantments() {
        List<ResourceLocation> unregisteredEnchantments = new ArrayList<>();
        for (var entry : BuiltInRegistries.ENCHANTMENT.keySet().stream().filter(id -> !ENCHANTMENT_TYPE_MAP.containsKey(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, id))).toList()) {
            registerEnchantmentType(new EnchantmentType<>(NoneEnchantmentConfiguration.CODEC, ResourceKey.create(Registries.ENCHANTMENT, entry)));
            unregisteredEnchantments.add(entry);
        }
        if (EnchantmentConfigUtil.getHelper().isDevelopmentEnvironment()) {
            StringBuilder builder = new StringBuilder("Enchantment Types have not been registered for enchantments:\n[\n");
            for (int i = 0; i < unregisteredEnchantments.size(); ++i) {
                builder.append("    ").append(unregisteredEnchantments.get(i));
                if (i < unregisteredEnchantments.size() - 1)
                    builder.append(",");
                builder.append("\n");
            }
            builder.append("]");
            builder.append("\nThese will still work with global fields, but your integration should include all enchantments.");
            builder.append("\nThis will not be displayed to the end user, if this does, please report this as a bug.");
            EnchantmentConfigUtil.LOGGER.warn(builder.toString());
        }
    }
}
