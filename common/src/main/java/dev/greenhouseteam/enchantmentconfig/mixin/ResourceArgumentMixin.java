package dev.greenhouseteam.enchantmentconfig.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourceArgument.class)
public class ResourceArgumentMixin<T> {
    @ModifyReturnValue(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/core/Holder$Reference;", at = @At(value = "RETURN"))
    private Holder.Reference<T> enchantmentconfig$throwOnDisabled(Holder.Reference<T> original) throws CommandSyntaxException {
        if (original.key().isFor(Registries.ENCHANTMENT) && original.is((TagKey<T>) EnchantmentConfigApi.DISABLED_ENCHANTMENT_TAG))
            throw new DynamicCommandExceptionType(
                    $$0 -> Component.translatableEscape("command.enchantmentconfig.disabled", $$0)
            ).create(original.key().location().toString());
        return original;
    }
}
