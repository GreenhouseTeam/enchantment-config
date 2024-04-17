package dev.greenhouseteam.enchantmentconfig.impl.vanilla;

import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigAssigner;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigEntrypoint;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigPlugin;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.DamageEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.configuration.NoneEnchantmentConfiguration;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.util.VanillaEnchantmentResourceKeys;
import dev.greenhouseteam.enchantmentconfig.impl.variable.ItemPredicateVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.MatchesVariable;
import dev.greenhouseteam.enchantmentconfig.impl.variable.MaxLevelVariable;
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

        assigner.registerVariableCodec(ItemPredicateVariable.ID, ItemPredicateVariable::staticCodec);
        assigner.registerVariableCodec(MatchesVariable.ID, VariableTypes.BOOLEAN, MatchesVariable.CODEC);
        assigner.registerVariableCodec(MaxLevelVariable.ID, VariableTypes.INT, MaxLevelVariable.CODEC);
        assigner.registerVariableCodec(OriginalVariable.ID, OriginalVariable::staticCodec);
    }
}
