package dev.greenhouseteam.enchantmentconfig.impl;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigEntrypoint;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistryKeys;
import dev.greenhouseteam.enchantmentconfig.impl.data.EnchantmentConfigLoader;
import dev.greenhouseteam.enchantmentconfig.platform.EnchantmentConfigPlatformHelperNeoForge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@Mod(EnchantmentConfigApi.MOD_ID)
public class EnchantmentConfigNeoForge {
    private static final EnchantmentConfigAssignerImpl ASSIGNER = new EnchantmentConfigAssignerImpl();

    public EnchantmentConfigNeoForge(IEventBus eventBus) {
        EnchantmentConfigApi.init(new EnchantmentConfigPlatformHelperNeoForge());
        getPlugins().forEach(plugin -> plugin.register(ASSIGNER));
        ASSIGNER.registerUnregisteredEnchantments();
    }

    /*
     * The MIT License (MIT)
     *
     * Copyright (c) 2014-2015 mezz
     *
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     *
     * The above copyright notice and this permission notice shall be included in
     * all copies or substantial portions of the Software.
     *
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
     * THE SOFTWARE.
     */
    private static List<EnchantmentConfigPlugin> getPlugins() {
        Type annotationType = Type.getType(EnchantmentConfigEntrypoint.class);
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        Set<String> pluginClassNames = new LinkedHashSet<>();
        for (ModFileScanData data : allScanData) {
            Iterable<ModFileScanData.AnnotationData> annotations = data.getAnnotations();
            for (ModFileScanData.AnnotationData a : annotations) {
                if (Objects.equals(a.annotationType(), annotationType))
                    pluginClassNames.add(a.memberName());
            }
        }
        List<EnchantmentConfigPlugin> plugins = new ArrayList<>();
        for (String className : pluginClassNames) {
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends EnchantmentConfigPlugin> constructor = asmClass.asSubclass(EnchantmentConfigPlugin.class);
                EnchantmentConfigPlugin plugin = constructor.newInstance();
                plugins.add(plugin);
            } catch (ReflectiveOperationException | LinkageError e) {
                EnchantmentConfigApi.LOGGER.error("Failed to load EnchantmentConfigEntrypoint: {}", className, e);
            }
        }
        return plugins;
    }

    @EventBusSubscriber(modid = EnchantmentConfigApi.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(EnchantmentConfigRegistries.CONDITION_CODEC);
            event.register(EnchantmentConfigRegistries.ENCHANTMENT_TYPE);
            event.register(EnchantmentConfigRegistries.VARIABLE_SERIALIZER);
            event.register(EnchantmentConfigRegistries.VARIABLE_TYPE);
        }

        @SubscribeEvent
        public static void onRegistration(RegisterEvent event) {
            if (event.getRegistryKey() == EnchantmentConfigRegistryKeys.ENCHANTMENT_TYPE_KEY)
                register(event, ASSIGNER::registerTypes);
            if (event.getRegistryKey() == EnchantmentConfigRegistryKeys.VARIABLE_TYPE)
                register(event, ASSIGNER::registerVariableTypes);
            if (event.getRegistryKey() == EnchantmentConfigRegistryKeys.VARIABLE_SERIALIZER_KEY)
                register(event, ASSIGNER::registerSerializers);
            if (event.getRegistryKey() == EnchantmentConfigRegistryKeys.CONDITION_CODEC_KEY)
                register(event, ASSIGNER::registerConditionCodecs);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegistrationCallback<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }
    }

    @EventBusSubscriber(modid = EnchantmentConfigApi.MOD_ID)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void modifyEnchantmentLevels(GetEnchantmentLevelEvent event) {
            if (EnchantmentConfigApi.getAndClearModificationType() == ModificationType.NO_CONFIGS)
                return;

            if (event.getTargetEnchant() != null) {
                setEnchantmentLevel(event.getEnchantments(), event.getStack(), event.getTargetEnchant());
                return;
            }

            for (EnchantmentType<?> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE) {
                Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
                if (enchantment == null)
                    continue;
                setEnchantmentLevel(event.getEnchantments(), event.getStack(), enchantment);
            }
        }

        private static void setEnchantmentLevel(ItemEnchantments.Mutable enchantments, ItemStack stack, Enchantment enchantment) {
            ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment);
            if (configured == null) return;
            int originalLevel = enchantments.getLevel(enchantment);
            enchantments.set(enchantment, configured.getGlobalFields().getOverrideLevel(originalLevel, enchantment, stack));
        }

        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new EnchantmentConfigLoader());
        }
    }
}