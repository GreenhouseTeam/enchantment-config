package dev.greenhouseteam.enchantmentconfig.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.impl.access.ItemEnchantmentsAccess;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin implements ItemEnchantmentsAccess {
    @Shadow @Final
    Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

    @Shadow @Mutable @Final public static Codec<ItemEnchantments> CODEC;

    @Unique
    private boolean enchantmentconfig$validate = false;

    @Accessor("showInTooltip")
    abstract boolean enchantmentconfig$getShowInTooltip();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void enchantmentconfig$validateEnchantmentsInCodec(CallbackInfo ci) {
        CODEC = CODEC.flatXmap(itemEnchantments -> {
                List<Holder<Enchantment>> disabledHolders = itemEnchantments.keySet().stream().filter(holder -> holder.is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG)).toList();
                if (!disabledHolders.isEmpty()) {
                    Object2IntOpenHashMap<Holder<Enchantment>> potentialNewMap = new Object2IntOpenHashMap<>();
                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                        if (!entry.getKey().is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                            potentialNewMap.addTo(entry.getKey(), entry.getIntValue());
                    }
                    var newItemEnchantments = new ItemEnchantments(potentialNewMap, ((ItemEnchantmentsMixin) (Object) itemEnchantments).enchantmentconfig$getShowInTooltip());
                    ((ItemEnchantmentsAccess) newItemEnchantments).enchantmentconfig$setToValidate();
                    if (disabledHolders.size() == 1)
                        return DataResult.error(() -> "Enchantment " + disabledHolders.getFirst() + " has been disabled via Enchantment Config.", newItemEnchantments);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < disabledHolders.size(); ++i) {
                        if (i == disabledHolders.size() - 1)
                            builder.append("and ");
                        builder.append(disabledHolders.get(i).getRegisteredName());
                        if (i < disabledHolders.size() - 1)
                            builder.append(", ");
                    }
                    return DataResult.error(() -> "Enchantments " + builder.toString() + " have been disabled via Enchantment Config.");
                }
                return DataResult.success(itemEnchantments);
        }, DataResult::success);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void enchantmentconfig$validateEnchantments(Object2IntOpenHashMap<Holder<Enchantment>> enchantments, boolean showInTooltip, CallbackInfo ci) {
        if (enchantmentconfig$validate)
            this.enchantments.keySet().removeIf(entry -> {
                if (entry.is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG)) {
                    EnchantmentConfigUtil.LOGGER.info("Removed enchantment {} from \"minecraft:enchantments\" component", entry.getRegisteredName());
                    enchantmentconfig$validate = false;
                    return true;
                }
                return false;
            });
    }

    @Override
    public void enchantmentconfig$setToValidate() {
        enchantmentconfig$validate = true;
    }
}
