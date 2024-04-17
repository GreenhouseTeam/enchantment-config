package dev.greenhouseteam.enchantmentconfig.impl;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.codec.VariableTypeCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.NoneEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableCodecFunction;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EnchantmentConfigAssignerImpl implements EnchantmentConfigAssigner {
    private static final Map<ResourceKey<EnchantmentType<?>>, EnchantmentType<?>> ENCHANTMENT_TYPE_MAP = new HashMap<>();
    private static final Map<ResourceLocation, VariableType<?>> ENCHANTMENT_VARIABLE_VARIABLE_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, VariableCodecFunction> ENCHANTMENT_VARIABLE_CODEC_MAP = new HashMap<>();

    public <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType) {
        ENCHANTMENT_TYPE_MAP.put(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, enchantmentType.getPath()), enchantmentType);
    }

    @Override
    public <T> void registerVariableCodec(ResourceLocation id, @Nullable VariableType<?> variableType, Function<VariableType<T>, MapCodec<? extends Variable<T>>> variableTypeToCodecFunction) {
        if (variableType != null)
            ENCHANTMENT_VARIABLE_VARIABLE_TYPES.put(id, variableType);
        ENCHANTMENT_VARIABLE_CODEC_MAP.put(id, type -> variableTypeToCodecFunction.apply((VariableType<T>) type));
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

    protected void registerCodecs(RegistrationCallback<VariableCodecFunction> callback) {
        ENCHANTMENT_VARIABLE_CODEC_MAP.forEach((id, codec) -> callback.register(EnchantmentConfigRegistries.VARIABLE_CODEC, id, codec));
        ENCHANTMENT_VARIABLE_VARIABLE_TYPES.forEach(VariableTypeCodec::addToVariableToTypeMap);
        ENCHANTMENT_VARIABLE_CODEC_MAP.clear();
        ENCHANTMENT_VARIABLE_VARIABLE_TYPES.clear();
    }

    protected void registerUnregisteredEnchantments() {
        List<ResourceLocation> unregisteredEnchantments = new ArrayList<>();
        for (var entry : BuiltInRegistries.ENCHANTMENT.keySet().stream().filter(id -> !ENCHANTMENT_TYPE_MAP.containsKey(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, id))).toList()) {
            registerEnchantmentType(new EnchantmentType<>(NoneEnchantmentConfiguration.CODEC, ResourceKey.create(Registries.ENCHANTMENT, entry)));
            unregisteredEnchantments.add(entry);
        }
        if (EnchantmentConfigUtil.getHelper().isDevelopmentEnvironment()) {
            EnchantmentConfigUtil.LOGGER.warn("Enchantment Types have not been registered for enchantments:");
            for (int i = 0; i < unregisteredEnchantments.size(); ++i) {
                StringBuilder builder = new StringBuilder();
                builder.append("\t").append(unregisteredEnchantments.get(i));
                if (i < unregisteredEnchantments.size() - 1)
                    builder.append(",");
                EnchantmentConfigUtil.LOGGER.warn(builder.toString());
            }
            EnchantmentConfigUtil.LOGGER.warn("These will still work with global fields, but your integration should include all enchantments.");
            EnchantmentConfigUtil.LOGGER.warn("This will not be displayed to the end user, if this does, please report this as a bug.");
        }
    }
}
