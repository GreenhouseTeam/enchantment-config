package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.ModificationType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public record EnchantmentLevelVariable(Holder<Enchantment> otherEnchantment, ModificationType modificationType) implements SingleTypedVariable<Integer> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("enchantment_level");
    public static final Serializer SERIALIZER = new Serializer();
    public static final MapCodec<EnchantmentLevelVariable> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().fieldOf("enchantment").forGetter(EnchantmentLevelVariable::otherEnchantment),
            StringRepresentable.fromEnum(ModificationType::values).optionalFieldOf("modification_type", ModificationType.ALL).forGetter(EnchantmentLevelVariable::modificationType)
    ).apply(inst, EnchantmentLevelVariable::new));

    @Override
    public Integer getValue(Enchantment enchantment, ItemStack stack, Integer original) {
        int returnValue = 0;
        if (otherEnchantment.isBound()) {
            return switch (modificationType) {
                case BEFORE -> stack.getEnchantments().getLevel(otherEnchantment.value());
                case CONFIG_ONLY -> {
                    ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(otherEnchantment.value());
                    if (configured != null)
                        yield configured.getGlobalFields().getOverrideLevel(original, otherEnchantment.value(), stack);
                    yield  0;
                }
                case NO_CONFIGS -> {
                    EnchantmentConfigApi.setModificationType(ModificationType.NO_CONFIGS);
                    yield EnchantmentHelper.getItemEnchantmentLevel(otherEnchantment.value(), stack);
                }
                default -> EnchantmentHelper.getItemEnchantmentLevel(otherEnchantment.value(), stack);
            };
        }
        return returnValue;
    }

    public boolean allowedInRootCondition() {
        return false;
    }

    @Override
    public VariableSerializer<Integer, Integer> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Integer> {
        @Override
        public VariableType<Integer> type(VariableType<?> inputType) {
            return VariableTypes.INT;
        }

        @Override
        public MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType) {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
