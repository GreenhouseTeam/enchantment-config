package dev.greenhouseteam.enchantmentconfig.impl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantmentConfig {
    private static RegistryAccess registries;
    private static List<TagManager.LoadResult<?>> tags;

    public static void setRegistryLookup(@Nullable RegistryAccess registries) {
        EnchantmentConfig.registries = registries;
    }

    public static void setTags(@Nullable List<TagManager.LoadResult<?>> loadResult) {
        EnchantmentConfig.tags = loadResult;
    }

    public static RegistryAccess getRegistryLookup() {
        return EnchantmentConfig.registries;
    }

    public static List<TagManager.LoadResult<?>> getTags() {
        return EnchantmentConfig.tags;
    }
}