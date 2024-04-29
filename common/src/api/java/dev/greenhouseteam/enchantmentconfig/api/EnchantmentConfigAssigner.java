package dev.greenhouseteam.enchantmentconfig.api;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.EnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ExtraFieldType;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public interface EnchantmentConfigAssigner {

    /**
     * Registers a {@link EnchantmentType} which can be defined as a configuration with special fields.
     * <p>
     * Configuration files can be created in <code>data/namespace/enchantmentconfig/configs/enchantment_namespace/enchantment_path.json</code>.
     * <p>
     * Otherwise, you may set a global configuration by placing a file within the <code>data/namespace/enchantmentconfig/global_configs</code> directory.
     *
     * @param enchantmentType   The EnchantmentType to register.
     * @param <T>               The EnchantmentType.
     * @param <C>               The EnchantmentConfiguration associated with this type.
     */
    <T extends EnchantmentType<C>, C extends EnchantmentConfiguration> void registerEnchantmentType(T enchantmentType);

    /**
     * Registers a {@link VariableType} for use within data.
     * These will pop up within
     *
     * @param type  The {@link VariableType} to register.
     * @param <T>   The value type of the {@link VariableType}.
     */
    <T> void registerVariableType(VariableType<T> type);

    /**
     * Registers a {@link VariableSerializer}, used to create dynamically modifiable values.
     *
     * @param serializer    The serializer to register.
     * @param <I>           The input value type of the serializer.
     * @param <O>           The output value type of the serializer.
     *
     * @see dev.greenhouseteam.enchantmentconfig.api.config.field.Field
     * @see dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable
     * @see dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable
     * @see dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer
     */
    <I, O> void registerVariableSerializer(VariableSerializer<I, O> serializer);

    /**
     * Adds an extra field to the specified enchantment config value.
     * If the specified enchantment is not present, this operation will be ignored.
     *
     * @param enchantmentKey    The {@link ResourceKey} of the {@link Enchantment} associated with the {@link EnchantmentType} used to add this value.
     * @param extraFieldType    The {@link ExtraFieldType} to associate with the specified enchantment config.
     */
    void addExtraField(ResourceKey<Enchantment> enchantmentKey, ExtraFieldType<?> extraFieldType);

    /**
     * Registers a condition codec for usage in root conditions.
     * Typically, you won't need this, as this mod provides what you should theoretically need.
     *
     * @param id        The ID of the condition codec, used within checking a value.
     * @param codec     The {@link MapCodec}.for the condition to serialize.
     * @param <T>       The condition.
     */
    <T extends Condition> void registerConditionCodec(ResourceLocation id, MapCodec<T> codec);

}
