package dev.greenhouseteam.enchantmentconfig.mixin.fabric;

import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
    private static int enchantmentconfig$getEnchantmentLevelOverrides(int original, Enchantment enchantment, ItemStack stack) {
        if (EnchantmentConfig.getAndClearModificationType() == ModificationType.NO_CONFIGS)
            return original;

        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment);
        if (configured == null)
            return original;

        return configured.getGlobalFields().getOverrideLevel(original, enchantment, stack);
    }

    @ModifyArg(method = "runIterationOnItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;accept(Lnet/minecraft/world/item/enchantment/Enchantment;I)V"), index = 1)
    private static int enchantmentconfig$overrideIterations(int original, @Local(argsOnly = true) ItemStack stack, @Local Entry<Holder<Enchantment>> entry) {
        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value());
        if (configured == null)
            return original;

        return configured.getGlobalFields().getOverrideLevel(original, entry.getKey().value(), stack);
    }
}
