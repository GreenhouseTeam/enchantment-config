package dev.greenhouseteam.enchantmentconfig.api.config;

import net.minecraft.util.StringRepresentable;

public enum ModificationType implements StringRepresentable {
    BEFORE("before"),
    CONFIG_ONLY("config_only"),
    NO_CONFIGS("no_configs"),
    ALL("all");

    private final String name;

    ModificationType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
