package dev.greenhouseteam.enchantmentconfig.mixin.fabric;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsPredicateAccess;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEnchantmentsPredicate.class)
public class ItemEnchantmentsPredicateMixin implements ItemEnchantmentsPredicateAccess {
    @Unique
    private boolean enchantmentconfig$noConfigs = false;

    @Inject(method = "matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/EnchantmentPredicate;containedIn(Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z", shift = At.Shift.BEFORE))
    private void enchantmentconfig$modifyPredicate(ItemStack stack, ItemEnchantments enchantments, CallbackInfoReturnable<Boolean> cir) {
        // Prevent StackOverflowErrors!
        if (enchantmentconfig$noConfigs)
            EnchantmentConfigApi.setModificationType(ModificationType.NO_CONFIGS);
    }

    @Override
    public void enchantmentconfig$setNoConfigs() {
        enchantmentconfig$noConfigs = true;
    }

    @ModifyVariable(method = "matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/EnchantmentPredicate;containedIn(Lnet/minecraft/world/item/enchantment/ItemEnchantments;)Z"), argsOnly = true)
    private ItemEnchantments enchantmentconfig$modifyPredicate(ItemEnchantments enchantments, ItemStack stack) {
        if (!((ItemEnchantmentsPredicate)(Object)this instanceof ItemEnchantmentsPredicate.Enchantments) || EnchantmentConfigApi.getAndClearModificationType() == ModificationType.NO_CONFIGS)
            return enchantments;

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        for (EnchantmentType<?> type : EnchantmentConfigRegistries.ENCHANTMENT_TYPE) {
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
            if (enchantment == null)
                continue;
            ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(type);
            if (configured == null)
                continue;
            int originalLevel = mutable.getLevel(enchantment);
            mutable.set(enchantment, configured.getGlobalFields().getOverrideLevel(originalLevel, enchantment, stack));
        }
        return mutable.toImmutable();
    }
}
