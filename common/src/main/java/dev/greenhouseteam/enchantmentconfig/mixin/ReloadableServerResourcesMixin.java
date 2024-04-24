package dev.greenhouseteam.enchantmentconfig.mixin;

import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Shadow @Final private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void enchantmentconfig$setServer(RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
        EnchantmentConfig.setRegistryLookup(this.registryLookup);
    }
}
