package dev.greenhouseteam.enchantmentconfig.mixin.neoforge;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsPredicateAccess;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEnchantmentsPredicate.class)
public class ItemEnchantmentsPredicateMixin implements ItemEnchantmentsPredicateAccess {
    @Unique
    private boolean enchantmentconfig$noConfigs = false;

    @Inject(method = "matches(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void enchantmentconfig$modifyPredicate(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Prevent StackOverflowErrors!
        if (enchantmentconfig$noConfigs)
            EnchantmentConfigApi.setModificationType(ModificationType.NO_CONFIGS);
    }

    @Override
    public void enchantmentconfig$setNoConfigs() {
        enchantmentconfig$noConfigs = true;
    }
}