package dev.greenhouseteam.enchantmentconfig.impl.vanilla;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigEntrypoint;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.config.condition.Condition;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.DamageEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.NoneEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.util.VanillaEnchantmentResourceKeys;
import dev.greenhouseteam.enchantmentconfig.impl.variable.EnchantmentLevelVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.ItemPredicateVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.MatchesVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.MaxLevelVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.ModifierVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.OriginalVariable;

@EnchantmentConfigEntrypoint
public class VanillaEnchantmentConfigEntry implements EnchantmentConfigPlugin {
    public static final EnchantmentType<NoneEnchantmentConfiguration> LOYALTY = new EnchantmentType<>(NoneEnchantmentConfiguration.CODEC, VanillaEnchantmentResourceKeys.LOYALTY);
    public static final EnchantmentType<DamageEnchantmentConfiguration> IMPALING = new EnchantmentType<>(DamageEnchantmentConfiguration.CODEC, VanillaEnchantmentResourceKeys.IMPALING);
    public static final EnchantmentType<DamageEnchantmentConfiguration> SHARPNESS = new EnchantmentType<>(DamageEnchantmentConfiguration.CODEC, VanillaEnchantmentResourceKeys.SHARPNESS);
    public static final EnchantmentType<DamageEnchantmentConfiguration> BANE_OF_ARTHROPODS = new EnchantmentType<>(DamageEnchantmentConfiguration.CODEC, VanillaEnchantmentResourceKeys.BANE_OF_ARTHROPODS);
    public static final EnchantmentType<DamageEnchantmentConfiguration> SMITE = new EnchantmentType<>(DamageEnchantmentConfiguration.CODEC, VanillaEnchantmentResourceKeys.SMITE);

    @Override
    public void register(EnchantmentConfigAssigner assigner) {
        assigner.registerEnchantmentType(LOYALTY);
        assigner.registerEnchantmentType(IMPALING);
        assigner.registerEnchantmentType(SHARPNESS);
        assigner.registerEnchantmentType(BANE_OF_ARTHROPODS);
        assigner.registerEnchantmentType(SMITE);

        assigner.registerVariableType(VariableTypes.BOOLEAN);
        assigner.registerVariableType(VariableTypes.DOUBLE);
        assigner.registerVariableType(VariableTypes.FLOAT);
        assigner.registerVariableType(VariableTypes.INT);
        assigner.registerVariableType(VariableTypes.LONG);

        assigner.registerVariableSerializer(EnchantmentLevelVariable.SERIALIZER);
        assigner.registerVariableSerializer(ItemPredicateVariable.SERIALIZER);
        assigner.registerVariableSerializer(MatchesVariable.SERIALIZER);
        assigner.registerVariableSerializer(MaxLevelVariable.SERIALIZER);
        assigner.registerVariableSerializer(ModifierVariable.SERIALIZER);
        assigner.registerVariableSerializer(OriginalVariable.SERIALIZER);

        assigner.registerConditionCodec(Condition.And.ID, Condition.And.CODEC);
        assigner.registerConditionCodec(Condition.Or.ID, Condition.Or.CODEC);
        assigner.registerConditionCodec(Condition.Xor.ID, Condition.Xor.CODEC);
        assigner.registerConditionCodec(Condition.CountIf.ID, Condition.CountIf.CODEC);
        assigner.registerConditionCodec(Condition.Not.ID, Condition.Not.CODEC);
        assigner.registerConditionCodec(Condition.Variable.ID, Condition.Variable.CODEC);
    }
}
