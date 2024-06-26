package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import dev.greenhouseteam.enchantmentconfig.impl.EnchantmentConfig;
import dev.greenhouseteam.enchantmentconfig.mixin.EnchantmentAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record MaxLevelVariable(Optional<Holder<Enchantment>> otherEnchantment, ModificationType modificationType, float percentage) implements SingleTypedVariable<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("max_level");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<MaxLevelVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("enchantment").forGetter(MaxLevelVariable::otherEnchantment),
            StringRepresentable.fromEnum(ModificationType::values).optionalFieldOf("modification_type", ModificationType.ALL).forGetter(MaxLevelVariable::modificationType),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("percentage", 1.0F).forGetter(MaxLevelVariable::percentage)
    ).apply(inst, MaxLevelVariable::new));

    @Override
    public Integer getValue(Enchantment enchantment, ItemStack stack, Integer original) {
        if (otherEnchantment.isPresent() && !otherEnchantment.get().isBound())
            return 0;

        Enchantment finalEnchantment = otherEnchantment.map(Holder::value).orElse(enchantment);

        return switch (modificationType) {
            case BEFORE -> ((EnchantmentAccessor) finalEnchantment).getDefinition().maxLevel();
            case CONFIG_ONLY -> {
                ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(finalEnchantment);
                if (configured != null)
                    yield configured.getGlobalFields().maxLevel().orElse(finalEnchantment.getMaxLevel());
                yield finalEnchantment.getMaxLevel();
            }
            case NO_CONFIGS -> {
                EnchantmentConfigApi.setModificationType(ModificationType.NO_CONFIGS);
                yield finalEnchantment.getMaxLevel();
            }
            default -> finalEnchantment.getMaxLevel();
        };
    }

    @Override
    public VariableSerializer<Integer, Integer> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Integer> {
        @Override
        public VariableType<Integer> type(@Nullable VariableType<?> contextType) {
            return VariableTypes.INT;
        }

        @Override
        public MapCodec<MaxLevelVariable> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
