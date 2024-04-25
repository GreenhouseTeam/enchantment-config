package dev.greenhouseteam.enchantmentconfig.mixin.neoforge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
    private boolean enchantmentconfig$allowEnchanting(boolean original, ItemStack stack) {
        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig((Enchantment)(Object)this);
        if (configured == null)
            return original;

        return configured.getGlobalFields().isApplicable(stack, original).orElse(original);
    }
}
