package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.GlobalEnchantmentFields;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "isCompatibleWith", at = @At("RETURN"))
    private boolean enchantmentconfig$modifyCompatibility(boolean original, Enchantment other) {
        return GlobalEnchantmentFields.isCompatible((Enchantment)(Object)this, other, original);
    }

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int enchantmentconfig$modifyMaxLevel(int original) {
        if (EnchantmentConfig.getAndClearModificationType() == ModificationType.NO_CONFIGS)
            return original;

        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig((Enchantment)(Object)this);
        if (configured == null)
            return original;

        return configured.getGlobalFields().maxLevel().orElse(original);
    }
}
