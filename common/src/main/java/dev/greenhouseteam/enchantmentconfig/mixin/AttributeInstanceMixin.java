package dev.greenhouseteam.enchantmentconfig.mixin;

import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AttributeInstance.class)
public class AttributeInstanceMixin {
    @Shadow @Final private Holder<Attribute> attribute;

    // Ignore the error, Fabric Mixin can do this.
    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;", shift = At.Shift.BEFORE), argsOnly = true)
    private Holder<Attribute> enchantmentconfig$dontSetBaseValue(Holder<Attribute> holder) {
        if (holder.unwrapKey().isPresent() && holder.unwrapKey().get().location().equals(EnchantmentConfigUtil.asResource("modifier_variable")))
            // Any attribute will work, but I'm choosing attack damage due to how integral it is to the game.
            return Attributes.ATTACK_DAMAGE;
        return holder;
    }

    @Inject(method = "calculateValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void enchantmentconfig$dontSetBaseValue(CallbackInfoReturnable<Double> cir, double baseValue, double finalValue) {
        if (attribute.unwrapKey().isPresent() && attribute.unwrapKey().get().location().equals(EnchantmentConfigUtil.asResource("modifier_variable")))
            cir.setReturnValue(finalValue);
    }
}
