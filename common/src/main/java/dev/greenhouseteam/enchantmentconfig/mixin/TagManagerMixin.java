package dev.greenhouseteam.enchantmentconfig.mixin;

import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import net.minecraft.tags.TagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TagManager.class)
public class TagManagerMixin {
    @Shadow private List<TagManager.LoadResult<?>> results;

    @Inject(method = { "method_40098", "lambda$reload$2" }, at = @At("TAIL"))
    private void enchantmentconfig$captureTags(List list, Void v, CallbackInfo ci) {
        EnchantmentConfig.setTags(results);
    }
}
