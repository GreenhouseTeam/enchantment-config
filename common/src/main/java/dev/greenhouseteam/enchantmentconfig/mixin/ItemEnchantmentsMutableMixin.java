package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsAccess;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEnchantments.Mutable.class)
public class ItemEnchantmentsMutableMixin {
    @ModifyReturnValue(method = "toImmutable", at = @At("RETURN"))
    private ItemEnchantments enchantmentconfig$validateMutableEnchantments(ItemEnchantments original) {
        ((ItemEnchantmentsAccess)original).enchantmentconfig$validate();
        return original;
    }
}
