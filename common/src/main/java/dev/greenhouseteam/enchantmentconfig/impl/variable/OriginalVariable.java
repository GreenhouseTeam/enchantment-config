package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record OriginalVariable<T>(VariableType<T> variableType) implements SingleTypedVariable<T> {
    public static final ResourceLocation ID = EnchantmentConfigApi.asResource("original");
    public static final Serializer SERIALIZER = new Serializer();

    public static MapCodec<OriginalVariable<Object>> staticCodec(VariableType<Object> variableType) {
        return MapCodec.unit(new OriginalVariable<>(variableType));
    }

    @Override
    public T getValue(Enchantment enchantment, ItemStack stack, T original) {
        return original;
    }


    public boolean allowedInRootCondition() {
        return false;
    }

    @Override
    public VariableSerializer<T, T> getSerializer() {
        return (VariableSerializer<T, T>) SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Object> {

        @Override
        public VariableType<Object> type(VariableType<?> inputType) {
            return (VariableType<Object>) inputType;
        }

        @Override
        public MapCodec<? extends Variable<?, ?>> codec(VariableType<Object> variableType) {
            return staticCodec(variableType);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
