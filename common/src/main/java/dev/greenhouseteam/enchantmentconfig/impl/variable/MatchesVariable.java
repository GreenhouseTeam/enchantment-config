package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record MatchesVariable(HolderSet<Enchantment> enchantments) implements SingleTypedVariable<Boolean> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("matches");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<MatchesVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(MatchesVariable::enchantments)
    ).apply(inst, MatchesVariable::new));

    @Override
    public Boolean getValue(Enchantment enchantment, ItemStack stack, Boolean original) {
        return enchantments.contains(enchantment.builtInRegistryHolder());
    }

    @Override
    public VariableSerializer<Boolean, Boolean> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Boolean> {
        @Override
        public VariableType<Boolean> type(VariableType<?> inputType) {
            return VariableTypes.BOOLEAN;
        }

        @Override
        public MapCodec<MatchesVariable> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
