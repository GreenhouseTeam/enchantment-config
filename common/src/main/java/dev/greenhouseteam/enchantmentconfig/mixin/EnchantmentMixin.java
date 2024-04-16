package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "isCompatibleWith", at = @At("RETURN"))
    private boolean enchantmentconfig$modifyCompatibility(boolean original, Enchantment other) {
        return EnchantmentConfigUtil.isCompatible((Enchantment)(Object)this, other).orElse(original);
    }

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int enchantmentconfig$modifyMaxLevel(int original) {
        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig((Enchantment)(Object)this, true);
        if (configured == null)
            configured = EnchantmentConfigGetter.INSTANCE.getConfig(EnchantmentConfigGetter.GLOBAL_KEY, false);

        return configured.getGlobalFields().maxLevel().orElse(original);
    }
}
