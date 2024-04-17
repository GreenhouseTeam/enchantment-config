package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

// TODO: Document this class.
public interface Variable<T> {
    static Codec<Variable<?>> dispatchCodec(VariableType<?> type) {
        return EnchantmentConfigRegistries.VARIABLE_CODEC.byNameCodec().xmap(function -> new TypedVariableCodecFunction(type, function), TypedVariableCodecFunction::getFunction).partialDispatch("type", variable -> DataResult.success(new TypedVariableCodecFunction(type, type1 -> variable.codec((VariableType<Object>) type1))), typedFunction -> DataResult.success(typedFunction.getFunction().create(type)));
    }

    T getValue(Enchantment enchantment, ItemStack stack, T original);


    MapCodec<? extends Variable<?>> codec(VariableType<Object> variableType);

    default boolean isComparable() {
        return true;
    }

    VariableType<T> variableType();

    ResourceLocation id();

}
