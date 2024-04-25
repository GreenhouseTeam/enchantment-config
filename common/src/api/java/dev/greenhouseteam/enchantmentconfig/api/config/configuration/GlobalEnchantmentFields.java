package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigGetter;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.ConfiguredEnchantment;
import dev.greenhouseteam.enchantmentconfig.api.config.field.Field;
import dev.greenhouseteam.enchantmentconfig.api.config.variable.VariableTypes;
import dev.greenhouseteam.enchantmentconfig.api.util.ExcludableHolderSet;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

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
 * @param compatibilities           Compatibilities with other enchantments.
 *                                  Can add or remove compatibilities using
 *                                  the {@link ExcludableHolderSet} logic.
 *                                  Leaving this empty results in default behavior.
 * @param enchantingTableWeight     Items of which this enchantment will show up for
 *                                  inside the enchanting table. Additionally, supports
 *                                  specifying NBT values which will require the item to
 *                                  match them to get the table.
 *                                  an NBT check before it can be enchanted.
 *                                  Leaving this empty results in default behavior.
 * @param applicableItems           The items that this enchantment can be applied to.
 *                                  Can add or remove items using the {@link ExcludableHolderSet}
 *                                  logic.
 * @param tradeable                 Whether this enchantment is tradeable by
 *                                  villagers.
 *                                  Leaving this empty results in default behavior.
 * @param treasure                  Whether this enchantment is considered treasure.
 *                                  Leaving this empty results in default behavior.
 */
public record GlobalEnchantmentFields(Optional<Integer> maxLevel,
                                      Map<Integer, Field<Integer, Integer>> effectivenessOverrides,
                                      Optional<ExcludableHolderSet<Enchantment>> compatibilities,
                                      Map<ItemPredicate, Integer> enchantingTableWeight,
                                      Optional<ExcludableHolderSet<Item>> applicableItems,
                                      Optional<Boolean> tradeable,
                                      Optional<Boolean> treasure,
                                      Optional<Holder<Enchantment>> replacement) {

    public static final MapCodec<GlobalEnchantmentFields> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("max_level", Codec.INT).forGetter(GlobalEnchantmentFields::maxLevel),
            EnchantmentConfigCodecs.rangeAllowedIntegerCodec("base_value", "new_value", EnchantmentConfigCodecs.fieldCodec(VariableTypes.INT)).optionalFieldOf("effectiveness_overrides",Map.of()).forGetter(GlobalEnchantmentFields::effectivenessOverrides),
            EnchantmentConfigCodecs.defaultableCodec("compatibilities", EnchantmentConfigCodecs.excludableHolderSetCodec(Registries.ENCHANTMENT)).forGetter(GlobalEnchantmentFields::compatibilities),
            EnchantmentConfigCodecs.mapCollectionCodec("item_predicate", "weight", ItemPredicate.CODEC, Codec.INT).optionalFieldOf("enchanting_table_weight", Map.of()).forGetter(GlobalEnchantmentFields::enchantingTableWeight),
            EnchantmentConfigCodecs.defaultableCodec("applicable_items", EnchantmentConfigCodecs.excludableHolderSetCodec(Registries.ITEM)).forGetter(GlobalEnchantmentFields::applicableItems),
            // TODO: Expand on tradeable field by utilising predicates and other stuff.
            EnchantmentConfigCodecs.defaultableCodec("tradeable", Codec.BOOL).forGetter(GlobalEnchantmentFields::tradeable),
            EnchantmentConfigCodecs.defaultableCodec("treasure", Codec.BOOL).forGetter(GlobalEnchantmentFields::treasure),
            BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("replacement").forGetter(GlobalEnchantmentFields::replacement)
    ).apply(inst, GlobalEnchantmentFields::new));

    public static boolean isCompatible(Enchantment enchantment, Enchantment other, boolean original) {
        Optional<Boolean> optional = checkCompatibility(enchantment, other, original);
        Optional<Boolean> otherOptional = checkCompatibility(other, enchantment, original);
        return optional.isPresent() && otherOptional.isPresent() ? optional.get() || otherOptional.get() : optional.orElseGet(() -> otherOptional.orElse(original));
    }

    private static Optional<Boolean> checkCompatibility(Enchantment enchantment, Enchantment other, boolean original) {
        ConfiguredEnchantment<?, ?> configured = EnchantmentConfigGetter.INSTANCE.getConfig(enchantment);
        if (configured == null)
            return Optional.empty();

        if (configured.getGlobalFields().compatibilities().isPresent()) {
            configured.getGlobalFields().compatibilities().get().setContext(original);
            return Optional.of(configured.getGlobalFields().compatibilities().get().contains(other.builtInRegistryHolder()));
        }
        return Optional.empty();
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

    public Optional<Boolean> isApplicable(ItemStack stack, boolean original) {
        return applicableItems.map(predicates -> {
            predicates.setContext(original);
            return predicates.contains(stack.getItemHolder());
        });
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

        Map<Integer, Field<Integer, Integer>> effectivenessOverrides = MergeUtil.mergeMap(effectivenessOverrides(), oldConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides), globalConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides));

        // TODO: Merge ExcludableHolderSets.

        Optional<ExcludableHolderSet<Enchantment>> compatibilities = MergeUtil.mergePrimitiveOptional(compatibilities(), oldConfiguration.flatMap(GlobalEnchantmentFields::compatibilities), globalConfiguration.flatMap(GlobalEnchantmentFields::compatibilities));

        Map<ItemPredicate, Integer> enchantingTableWeight = MergeUtil.mergeMap(enchantingTableWeight(), oldConfiguration.map(GlobalEnchantmentFields::enchantingTableWeight), globalConfiguration.map(GlobalEnchantmentFields::enchantingTableWeight));

        Optional<ExcludableHolderSet<Item>> applicablePredicates = MergeUtil.mergePrimitiveOptional(applicableItems(), oldConfiguration.flatMap(GlobalEnchantmentFields::applicableItems), globalConfiguration.flatMap(GlobalEnchantmentFields::applicableItems));

        Optional<Boolean> tradeable = MergeUtil.mergePrimitiveOptional(tradeable(), oldConfiguration.flatMap(GlobalEnchantmentFields::tradeable), globalConfiguration.flatMap(GlobalEnchantmentFields::tradeable));
        Optional<Boolean> treasure = MergeUtil.mergePrimitiveOptional(treasure(), oldConfiguration.flatMap(GlobalEnchantmentFields::treasure), globalConfiguration.flatMap(GlobalEnchantmentFields::treasure));

        Optional<Holder<Enchantment>> replacement = MergeUtil.mergePrimitiveOptional(replacement(), oldConfiguration.flatMap(GlobalEnchantmentFields::replacement), globalConfiguration.flatMap(GlobalEnchantmentFields::replacement));

        return new GlobalEnchantmentFields(maxLevel, effectivenessOverrides, compatibilities, enchantingTableWeight, applicablePredicates, tradeable, treasure, replacement);
    }
}
