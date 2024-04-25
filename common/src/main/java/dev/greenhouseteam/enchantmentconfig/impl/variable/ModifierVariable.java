package dev.greenhouseteam.enchantmentconfig.impl.variable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.SingleTypedVariable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.Variable;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableSerializer;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.NumberVariableType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.type.VariableType;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModifierVariable implements SingleTypedVariable<Number> {
    public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("modifier");
    public static final Serializer SERIALIZER = new Serializer();

    public static MapCodec<ModifierVariable> staticCodec(VariableType<Object> variableType) {
        if (variableType == null) {
            variableType = (VariableType<Object>)(Object)VariableTypes.DOUBLE;
        } else if (!((VariableType<?>)variableType instanceof NumberVariableType<?>))
            throw new UnsupportedOperationException("Could not use non number variabletype for ModifierVariable.");
        VariableType<Number> finalVariableType = (VariableType<Number>)(Object)variableType;
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentConfigCodecs.fieldCodec(finalVariableType).optionalFieldOf("base", getDefaultBaseField(finalVariableType)).forGetter(ModifierVariable::base),
                EnchantmentConfigCodecs.fieldCodec(finalVariableType).fieldOf("value").xmap(field -> field, field -> field).forGetter(ModifierVariable::modifier),
                AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(ModifierVariable::operation)
        ).apply(inst, ModifierVariable::new));
    }

    private static Field<Number, Number> getDefaultBaseField(VariableType<Number> type) {
        return new Field<>(new OriginalVariable<>(type), type);
    }

    private final Field<Number, Number> base;
    private final Field<Number, Number> modifier;
    private final AttributeModifier.Operation operation;
    private AttributeInstance instance;

    public ModifierVariable(Field<Number, Number> base, Field<Number, Number> modifier, AttributeModifier.Operation operation) {
        this.base = base;
        this.modifier = modifier;
        this.operation = operation;
    }

    @Override
    public Number getValue(Enchantment enchantment, ItemStack stack, Number original) {
        if (instance == null) {
            // Use attribute as
            AttributeInstance instance = new AttributeInstance(Holder.Reference.createStandAlone(BuiltInRegistries.ATTRIBUTE.asLookup(), ResourceKey.create(Registries.ATTRIBUTE, EnchantmentConfigUtil.asResource("modifier_variable"))),attributeInstance -> {});
            instance.setBaseValue(base.getDouble(enchantment, stack, original));
            instance.addPermanentModifier(new AttributeModifier("Attribute for ModifierVariable", modifier.getDouble(enchantment, stack, original), operation));
            this.instance = instance;
        }
        return instance.getValue();
    }

    public Field<Number, Number> base() {
        return base;
    }

    public Field<Number, Number> modifier() {
        return modifier;
    }

    public AttributeModifier.Operation operation() {
        return operation;
    }

    public boolean allowedInRootCondition() {
        return base.getInnerVariable().allowedInRootCondition() && modifier.getInnerVariable().allowedInRootCondition();
    }

    @Override
    public VariableSerializer<Number, Number> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends SingleTypedSerializer<Number> {

        @Override
        public VariableType<Number> type(VariableType<?> inputType) {
            return (VariableType<Number>) inputType;
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
