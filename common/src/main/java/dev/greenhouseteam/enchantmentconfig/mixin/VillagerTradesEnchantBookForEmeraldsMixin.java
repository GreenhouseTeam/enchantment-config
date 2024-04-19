package dev.greenhouseteam.enchantmentconfig.mixin;

import com.google.common.collect.ImmutableList;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class VillagerTradesEnchantBookForEmeraldsMixin {
    @Shadow @Mutable @Final
    private List<Enchantment> tradeableEnchantments;

    @Mutable
    @Shadow @Final private int minLevel;

    @Inject(method = "<init>(III[Lnet/minecraft/world/item/enchantment/Enchantment;)V", at = @At("TAIL"))
    private void enchantmentconfig$disableFromVillagerTrades(int minLevel, int maxLevel, int villagerXp, Enchantment[] enchantments, CallbackInfo ci) {
        List<Enchantment> finalEnchantments = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            if (!enchantment.builtInRegistryHolder().is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                finalEnchantments.add(enchantment);
            else if (EnchantmentConfigGetter.INSTANCE.getConfig(enchantment, true) != null && EnchantmentConfigGetter.INSTANCE.getConfig(enchantment, true).getGlobalFields().replacement().isPresent()) {
                finalEnchantments.add(EnchantmentConfigGetter.INSTANCE.getConfig(enchantment, true).getGlobalFields().replacement().get().value());
                if (enchantment.getMaxLevel() < minLevel)
                    this.minLevel = enchantment.getMaxLevel();
            }
        }
        if (finalEnchantments.isEmpty()) {
            // This only affects the villager rework.
            this.tradeableEnchantments = Arrays.asList(BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).toArray(Enchantment[]::new));
            // This should guarantee the same level as the villager rework, or lower if below the required level.
            if (tradeableEnchantments.get(0).getMaxLevel() < minLevel)
                this.minLevel = tradeableEnchantments.get(0).getMaxLevel();
        } else
            this.tradeableEnchantments = ImmutableList.copyOf(finalEnchantments);
    }
}
