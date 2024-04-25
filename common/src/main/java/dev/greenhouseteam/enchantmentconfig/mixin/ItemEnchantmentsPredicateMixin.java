package dev.greenhouseteam.enchantmentconfig.mixin;

import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsPredicateAccess;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEnchantmentsPredicate.class)
public class ItemEnchantmentsPredicateMixin implements ItemEnchantmentsPredicateAccess {
    @Unique
    private boolean enchantmentconfig$noConfigs = false;

    @Inject(method = "matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/EnchantmentPredicate;containedIn(Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z"), cancellable = true)
    private void enchantmentconfig$modifyPredicate(ItemStack stack, ItemEnchantments enchantments, CallbackInfoReturnable<Boolean> cir) {
        // Prevent StackOverflowErrors!
        if (enchantmentconfig$noConfigs)
            EnchantmentConfig.setModificationType(ModificationType.NO_CONFIGS);
    }

    @Override
    public void enchantmentconfig$setNoConfigs() {
        enchantmentconfig$noConfigs = true;
    }
}
