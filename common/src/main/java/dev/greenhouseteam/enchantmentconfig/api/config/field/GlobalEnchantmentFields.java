package dev.greenhouseteam.enchantmentconfig.api.config.field;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
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
 * @param discoverable              Whether this enchantment is discoverable through
 *                                  an enchanting table.
 *                                  Leaving this empty results in default behavior.
 * @param enchantingWeight          The weight at which this enchantment will
 *                                  appear in the enchanting table.
 *                                  Leaving this empty results in default behavior.
 * @param tradeable                 Whether this enchantment is tradeable by
 *                                  villagers.
 *                                  Leaving this empty results in default behavior.
 * @param treasure                  Whether this enchantment is considered treasure.
 *                                  Leaving this empty results in default behavior.
 */
public record GlobalEnchantmentFields(Optional<Integer> maxLevel,
                                      Map<Integer, Integer> effectivenessOverrides,
                                      Optional<List<HolderSet<Enchantment>>> incompatibilities,
                                      Optional<Boolean> discoverable,
                                      Optional<Integer> enchantingWeight,
                                      Optional<Boolean> tradeable,
                                      Optional<Boolean> treasure) {

    public static MapCodec<GlobalEnchantmentFields> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("max_level", EnchantmentConfigCodecs.INT).forGetter(GlobalEnchantmentFields::maxLevel),
            EnchantmentConfigCodecs.mapCollectionCodec("base_value", "new_value", EnchantmentConfigCodecs.INT, EnchantmentConfigCodecs.INT).optionalFieldOf("effectiveness_overrides", Map.of()).forGetter(GlobalEnchantmentFields::effectivenessOverrides),
            EnchantmentConfigCodecs.defaultableCodec("incompatibilities", Codec.list(EnchantmentConfigCodecs.tagOrElementCodec(Registries.ENCHANTMENT))).forGetter(GlobalEnchantmentFields::incompatibilities),
            EnchantmentConfigCodecs.defaultableCodec("discoverable", EnchantmentConfigCodecs.BOOLEAN).forGetter(GlobalEnchantmentFields::discoverable),
            EnchantmentConfigCodecs.defaultableCodec("enchanting_weight", EnchantmentConfigCodecs.INT).forGetter(GlobalEnchantmentFields::enchantingWeight),
            EnchantmentConfigCodecs.defaultableCodec("tradeable", EnchantmentConfigCodecs.BOOLEAN).forGetter(GlobalEnchantmentFields::tradeable),
            EnchantmentConfigCodecs.defaultableCodec("treasure", EnchantmentConfigCodecs.BOOLEAN).forGetter(GlobalEnchantmentFields::treasure)
    ).apply(inst, GlobalEnchantmentFields::new));

    /**
     * Defines how to merge this enchantment configuration into another one and the global default.
     *
     * @param oldConfiguration      The original configuration to merge into this config.
     * @param globalConfiguration   The global configuration for merging the global value
     *                              into this config. Is not always present and should
     *                              be ignored when so.
     * @param priority              The priority of the current merge.
     * @param oldPriority           The value at which the priority must be higher than to
     *                              have the current value be merged if it is present.
     * @param globalPriority        The value at which the priority must be lower than to
     *                              have the global value be merged if it is present.
     *
     * @return                      A merged EnchantmentConfiguration.
     */
    public GlobalEnchantmentFields merge(GlobalEnchantmentFields oldConfiguration, Optional<GlobalEnchantmentFields> globalConfiguration, int priority, int oldPriority, int globalPriority) {


        Optional<Integer> maxLevel = MergeUtil.mergePrimitiveOptional(this.maxLevel(), oldConfiguration.maxLevel(), globalConfiguration.flatMap(GlobalEnchantmentFields::maxLevel), priority, oldPriority, globalPriority);

        Map<Integer, Integer> effectivenessOverrides = MergeUtil.mergeMap(this.effectivenessOverrides(), oldConfiguration.effectivenessOverrides(), globalConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides), priority, oldPriority, globalPriority);

        // TODO: Create a MergeUtil method for merging lists.
        Optional<List<HolderSet<Enchantment>>> incompatibilities = Optional.empty();
        if (globalConfiguration.isEmpty())
            incompatibilities = this.incompatibilities().isEmpty() ? oldConfiguration.incompatibilities() : this.incompatibilities();
        else if (globalConfiguration.get().incompatibilities().isPresent() && globalPriority > priority)
            incompatibilities = globalConfiguration.get().incompatibilities();

        Optional<Boolean> discoverable = MergeUtil.mergePrimitiveOptional(this.discoverable(), oldConfiguration.discoverable(), globalConfiguration.flatMap(GlobalEnchantmentFields::discoverable), priority, oldPriority, globalPriority);
        Optional<Integer> enchantingWeight = MergeUtil.mergePrimitiveOptional(this.enchantingWeight(), oldConfiguration.enchantingWeight(), globalConfiguration.flatMap(GlobalEnchantmentFields::enchantingWeight), priority, oldPriority, globalPriority);
        Optional<Boolean> tradeable = MergeUtil.mergePrimitiveOptional(this.tradeable(), oldConfiguration.tradeable(), globalConfiguration.flatMap(GlobalEnchantmentFields::tradeable), priority, oldPriority, globalPriority);
        Optional<Boolean> treasure = MergeUtil.mergePrimitiveOptional(this.treasure(), oldConfiguration.treasure(), globalConfiguration.flatMap(GlobalEnchantmentFields::treasure), priority, oldPriority, globalPriority);

        return new GlobalEnchantmentFields(maxLevel, effectivenessOverrides, incompatibilities, discoverable, enchantingWeight, tradeable, treasure);
    }
}
