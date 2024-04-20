package dev.greenhouseteam.enchantmentconfig.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin implements ItemEnchantmentsAccess {
    @Shadow @Final
    Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

    @Shadow @Mutable @Final public static Codec<ItemEnchantments> CODEC;

    @Accessor("showInTooltip")
    abstract boolean enchantmentconfig$getShowInTooltip();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void enchantmentconfig$validateEnchantmentsInCodec(CallbackInfo ci) {
        CODEC = CODEC.flatXmap(itemEnchantments -> {
            List<Holder<Enchantment>> disabledHolders = new ArrayList<>(itemEnchantments.keySet().stream().filter(holder -> holder.is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG)).toList());
            if (!disabledHolders.isEmpty()) {
                Object2IntOpenHashMap<Holder<Enchantment>> potentialNewMap = new Object2IntOpenHashMap<>();
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                    if (!entry.getKey().is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG))
                        potentialNewMap.addTo(entry.getKey(), entry.getIntValue());
                    else if (entry.getKey().isBound() && EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value(), true) != null && EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value(), true).getGlobalFields().replacement().isPresent()) {
                        potentialNewMap.addTo(EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value(), true).getGlobalFields().replacement().get(), entry.getIntValue());
                        disabledHolders.remove(entry.getKey());
                    }
                }
                var newItemEnchantments = new ItemEnchantments(potentialNewMap, ((ItemEnchantmentsMixin) (Object) itemEnchantments).enchantmentconfig$getShowInTooltip());
                if (disabledHolders.size() == 1)
                    return DataResult.error(() -> "Enchantment " + disabledHolders.getFirst().getRegisteredName() + " has been disabled via the enchantmentconfig:disabled enchantment tag.", newItemEnchantments);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < disabledHolders.size(); ++i) {
                    if (i == disabledHolders.size() - 1)
                        builder.append("and ");
                    builder.append(disabledHolders.get(i).getRegisteredName());
                    if (i < disabledHolders.size() - 1)
                        builder.append(", ");
                }
                return DataResult.error(() -> "Enchantments " + builder.toString() + " have been disabled via the enchantmentconfig:disabled enchantment tag.");
            }
            return DataResult.success(itemEnchantments);
        }, DataResult::success);
    }

    @Override
    public void enchantmentconfig$validate() {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : this.enchantments.object2IntEntrySet()) {
            if (entry.getKey().is(EnchantmentConfigUtil.DISABLED_ENCHANTMENT_TAG)) {
                EnchantmentConfigUtil.LOGGER.info("Removed enchantment {} from \"minecraft:enchantments\" component", entry.getKey().getRegisteredName());
                this.enchantments.remove(entry, entry.getIntValue());
                if (entry.getKey().isBound() && EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value(), true).getGlobalFields().replacement().isPresent()) {
                    this.enchantments.addTo(EnchantmentConfigGetter.INSTANCE.getConfig(entry.getKey().value(), true).getGlobalFields().replacement().get(), entry.getIntValue());
                }
            }
        }
    }
}
