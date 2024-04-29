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

    /**
     * Gets the output value of this variable.
     *
     * @param enchantment   The {@link  Enchantment} to work with.
     * @param stack         The {@link ItemStack} to work with.
     * @param original      The input value called by this variable.
     * @return              An output value.
     *
     * @see dev.greenhouseteam.enchantmentconfig.api.config.field.Field
     */
    O getValue(Enchantment enchantment, ItemStack stack, I original);

    /**
     * Whether this variable is allowed within root conditions.
     * Typically, you want to set this to false if you are utilising
     * an {@link ItemStack} or an original value.
     *
     * @return Whether this variable is allowed within root conditions.
     */
    default boolean allowedInRootCondition() {
        return true;
    }

    VariableSerializer<I, O> getSerializer();

}
