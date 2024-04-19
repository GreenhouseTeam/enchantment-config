package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A class with common fields for all enchantment configurations.
 *
 * @param maxLevel                  The maximum level which this enchantment can reach.
 * @param effectivenessOverrides    Overrides for level effectiveness.
 *                                  For example, you may want to set an enchantment
 *                                  to be at max level prior to it.
 *                                  Leaving this empty results in default behavior.
 * @param incompatibilities         Incompatibilities with other enchantments,
 *                                  can add or remove incompatibilities by
 *                                  specifying/not specifying them respectively.
 *                                  Leaving this empty results in default behavior.
 * @param enchantingTableWeight     Items of which this enchantment will show up for
 *                                  inside the enchanting table. Additionally, supports
 *                                  specifying NBT values which will require the item to
 *                                  match them to get the table.
 *                                  an NBT check before it can be enchanted.
 *                                  Leaving this empty results in default behavior.
 * @param tradeable                 Whether this enchantment is tradeable by
 *                                  villagers.
 *                                  Leaving this empty results in default behavior.
 * @param treasure                  Whether this enchantment is considered treasure.
 *                                  Leaving this empty results in default behavior.
 */
public record GlobalEnchantmentFields(Optional<Integer> maxLevel,
                                      Map<Integer, Field<Integer>> effectivenessOverrides,
                                      Optional<List<HolderSet<Enchantment>>> incompatibilities,
                                      Map<ItemPredicate, Integer> enchantingTableWeight,
                                      Optional<Boolean> tradeable,
                                      Optional<Boolean> treasure,
                                      Optional<Holder<Enchantment>> replacement) {

    public static final MapCodec<GlobalEnchantmentFields> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("max_level", Codec.INT).forGetter(GlobalEnchantmentFields::maxLevel),
            EnchantmentConfigCodecs.rangeAllowedIntegerCodec("base_value", "new_value", EnchantmentConfigCodecs.fieldCodec(VariableTypes.INT)).optionalFieldOf("effectiveness_overrides",Map.of()).forGetter(GlobalEnchantmentFields::effectivenessOverrides),
            EnchantmentConfigCodecs.defaultableCodec("incompatibilities", Codec.list(RegistryCodecs.homogeneousList(Registries.ENCHANTMENT))).forGetter(GlobalEnchantmentFields::incompatibilities),
            EnchantmentConfigCodecs.mapCollectionCodec("item_predicate", "weight", ItemPredicate.CODEC, Codec.INT).optionalFieldOf("enchanting_table_weight", Map.of()).forGetter(GlobalEnchantmentFields::enchantingTableWeight),
            // TODO: Expand on tradeable field by utilising predicates and other stuff.
            EnchantmentConfigCodecs.defaultableCodec("tradeable", Codec.BOOL).forGetter(GlobalEnchantmentFields::tradeable),
            EnchantmentConfigCodecs.defaultableCodec("treasure", Codec.BOOL).forGetter(GlobalEnchantmentFields::treasure),
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("replacement").forGetter(GlobalEnchantmentFields::replacement)
    ).apply(inst, GlobalEnchantmentFields::new));

    public static Optional<Boolean> isCompatible(Enchantment enchantment, Enchantment other) {
        Optional<Boolean> optional = checkCompatibility(enchantment, other);
        Optional<Boolean> otherOptional = checkCompatibility(other, enchantment);
        return optional.isPresent() && otherOptional.isPresent() ? Optional.of(optional.get() && otherOptional.get()) : optional.isPresent() ? optional : otherOptional;
    }

    private static Optional<Boolean> checkCompatibility(Enchantment enchantment, Enchantment other) {
        ConfiguredEnchantment<?, ?> type = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment, true);
        if (type == null)
            return Optional.empty();

        return EnchantmentConfigGetter.INSTANCE.getConfig(type.getType()).getGlobalFields().incompatibilities().isPresent() ? Optional.of(EnchantmentConfigGetter.INSTANCE.getConfig(type.getType()).getGlobalFields().incompatibilities().get().stream().noneMatch(holders -> holders.contains(other.builtInRegistryHolder()))) : Optional.empty();
    }

    public int getOverrideLevel(int level, Enchantment enchantment, ItemStack stack) {
        if (!effectivenessOverrides().isEmpty()) {
            if (effectivenessOverrides().containsKey(level))
                return effectivenessOverrides().get(level).getInt(enchantment, stack, level);

            if (effectivenessOverrides().keySet().stream().allMatch(i -> i > level))
                return level;

            if (effectivenessOverrides().keySet().stream().allMatch(i -> i < level)) {
                int value = effectivenessOverrides().keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();
                return effectivenessOverrides().containsKey(value) ? effectivenessOverrides().get(value).getInt(enchantment, stack, level) : level;
            }

            int upperBound = effectivenessOverrides().keySet().stream().filter(i -> i > level).min(Integer::compareTo).orElseThrow();
            int lowerBound = effectivenessOverrides().keySet().stream().filter(i -> i < level).max(Integer::compareTo).orElseThrow();

            int upperLevel = effectivenessOverrides().containsKey(upperBound) ? effectivenessOverrides().get(upperBound).getInt(enchantment, stack, level) : level;
            int lowerLevel = effectivenessOverrides().containsKey(lowerBound) ? effectivenessOverrides().get(lowerBound).getInt(enchantment, stack, level) : level;

            if (lowerLevel == upperLevel) {
                return lowerLevel;
            }

            return (int) Mth.lerp(lowerLevel, upperLevel, (float) (level - lowerBound) / (upperBound - lowerBound));
        }

        return level;
    }

    /**
     * Defines how to merge this enchantment configuration into another one and the global default.
     *
     * @param oldConfiguration      The original configuration to merge into this config.
     * @param globalConfiguration   The global configuration for merging the global value
     *                              into this config. Is not always present and should
     *                              be ignored when so.
     *
     * @return                      A merged EnchantmentConfiguration.
     */
    public GlobalEnchantmentFields merge(Optional<GlobalEnchantmentFields> oldConfiguration, Optional<GlobalEnchantmentFields> globalConfiguration) {
        Optional<Integer> maxLevel = MergeUtil.mergePrimitiveOptional(maxLevel(), oldConfiguration.flatMap(GlobalEnchantmentFields::maxLevel), globalConfiguration.flatMap(GlobalEnchantmentFields::maxLevel));

        Map<Integer, Field<Integer>> effectivenessOverrides = MergeUtil.mergeMap(effectivenessOverrides(), oldConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides), globalConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides));

        Optional<List<HolderSet<Enchantment>>> incompatibilities = MergeUtil.mergeOptionalList(incompatibilities(), oldConfiguration.flatMap(GlobalEnchantmentFields::incompatibilities), globalConfiguration.flatMap(GlobalEnchantmentFields::incompatibilities));

        Map<ItemPredicate, Integer> enchantingTableWeight = MergeUtil.mergeMap(enchantingTableWeight(), oldConfiguration.map(GlobalEnchantmentFields::enchantingTableWeight), globalConfiguration.map(GlobalEnchantmentFields::enchantingTableWeight));

        Optional<Boolean> tradeable = MergeUtil.mergePrimitiveOptional(tradeable(), oldConfiguration.flatMap(GlobalEnchantmentFields::tradeable), globalConfiguration.flatMap(GlobalEnchantmentFields::tradeable));
        Optional<Boolean> treasure = MergeUtil.mergePrimitiveOptional(treasure(), oldConfiguration.flatMap(GlobalEnchantmentFields::treasure), globalConfiguration.flatMap(GlobalEnchantmentFields::treasure));

        Optional<Holder<Enchantment>> replacement = MergeUtil.mergePrimitiveOptional(replacement(), oldConfiguration.flatMap(GlobalEnchantmentFields::replacement), globalConfiguration.flatMap(GlobalEnchantmentFields::replacement));

        return new GlobalEnchantmentFields(maxLevel, effectivenessOverrides, incompatibilities, enchantingTableWeight, tradeable, treasure, replacement);
    }
}
