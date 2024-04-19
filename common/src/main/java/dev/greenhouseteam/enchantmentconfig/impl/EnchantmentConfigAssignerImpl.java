package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.NoneEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
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
    private static final Map<ResourceLocation, VariableSerializer<?, ?>> VARIABLE_SERIALIZER_MAP = new HashMap<>();
    private static final Map<ResourceKey<EnchantmentType<?>>, List<ExtraFieldType<?>>> EXTRA_FIELD_MAP = new HashMap<>();

    public <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType) {
        ENCHANTMENT_TYPE_MAP.put(ResourceKey.create(EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY, enchantmentType.getPath()), enchantmentType);
    }

    @Override
    public <I, O> void registerVariable(VariableSerializer<I, O> serializer) {
        VARIABLE_SERIALIZER_MAP.put(serializer.id(), serializer);
    }

    @Override
    public void addExtraField(ResourceKey<EnchantmentType<?>> enchantmentType, ExtraFieldType<?> extraFieldType) {
        EXTRA_FIELD_MAP.computeIfAbsent(enchantmentType, enchantmentTypeResourceKey -> new ArrayList<>()).add(extraFieldType);
    }

    protected void registerTypes(RegistrationCallback<EnchantmentType<?>> callback) {
        ENCHANTMENT_TYPE_MAP.forEach((key, type) -> {
            if (EXTRA_FIELD_MAP.containsKey(key)) {
                for (ExtraFieldType<?> extraFieldType : EXTRA_FIELD_MAP.get(key)) {
                    type.addExtraFieldType(extraFieldType.key(), extraFieldType);
                    EXTRA_FIELD_MAP.get(key).remove(extraFieldType);
                }
            }
            callback.register(EnchantmentConfigRegistries.ENCHANTMENT_TYPE, key.location(), type);
        });
        ENCHANTMENT_TYPE_MAP.clear();
        EXTRA_FIELD_MAP.clear();
    }

    protected void registerCodecs(RegistrationCallback<VariableSerializer<?, ?>> callback) {
        VARIABLE_SERIALIZER_MAP.forEach((id, serializer) -> callback.register(EnchantmentConfigRegistries.VARIABLE_SERIALIZER, id, serializer));
        VARIABLE_SERIALIZER_MAP.clear();
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
