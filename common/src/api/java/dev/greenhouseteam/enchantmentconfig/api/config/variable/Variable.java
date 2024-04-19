package dev.greenhouseteam.enchantmentconfig.api.config.variable;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

// TODO: Document this class.
public interface Variable<I, O> {
    static Codec<Variable<?, ?>> dispatchCodec(VariableType<?> type) {
        return EnchantmentConfigRegistries.VARIABLE_SERIALIZER.byNameCodec().dispatch(Variable::getSerializer, variableSerializer -> variableSerializer.codec((VariableType<Object>) variableSerializer.inputType(type)));
    }

    O getValue(Enchantment enchantment, ItemStack stack, I original);

    default boolean allowedInRootCondition() {
        return true;
    }

    VariableSerializer<I, O> getSerializer();

}
