package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.BooleanVariable;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record IsVariable(HolderSet<Enchantment> enchantments) implements BooleanVariable {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("is");
    public static final MapCodec<IsVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(IsVariable::enchantments)
    ).apply(inst, IsVariable::new));

    @Override
    public Boolean getValue(Enchantment enchantment, ItemStack stack, Boolean original) {
        return enchantments().contains(enchantment.builtInRegistryHolder());
    }

    @Override
    public MapCodec<IsVariable> codec() {
        return CODEC;
    }
}
