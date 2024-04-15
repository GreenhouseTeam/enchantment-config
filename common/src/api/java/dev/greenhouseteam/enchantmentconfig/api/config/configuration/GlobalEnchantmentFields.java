package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.config.field.ItemAndTagMix;
import dev.greenhouseteam.enchantmentconfig.api.config.field.VariableField;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
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
                                      Map<Integer, VariableField<Integer>> effectivenessOverrides,
                                      Optional<List<HolderSet<Enchantment>>> incompatibilities,
                                      Map<ItemAndTagMix, Integer> enchantingTableWeight,
                                      Optional<Boolean> tradeable,
                                      Optional<Boolean> treasure) {

    public static final MapCodec<GlobalEnchantmentFields> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("max_level", Codec.INT).forGetter(GlobalEnchantmentFields::maxLevel),
            EnchantmentConfigCodecs.rangeAllowedIntegerCodec("base_value", "new_value", EnchantmentConfigCodecs.variableFieldCodec(Codec.INT, Integer.class)).optionalFieldOf("effectiveness_overrides", Map.of()).forGetter(GlobalEnchantmentFields::effectivenessOverrides),
            EnchantmentConfigCodecs.defaultableCodec("incompatibilities", Codec.list(RegistryCodecs.homogeneousList(Registries.ENCHANTMENT, BuiltInRegistries.ENCHANTMENT.byNameCodec()))).forGetter(GlobalEnchantmentFields::incompatibilities),
            EnchantmentConfigCodecs.mapCollectionCodec("stack", "weight", ItemAndTagMix.CODEC, Codec.INT).optionalFieldOf("enchanting_table_weight", Map.of()).forGetter(GlobalEnchantmentFields::enchantingTableWeight),
            // TODO: Expand on tradeable field by utilising predicates and other stuff.
            EnchantmentConfigCodecs.defaultableCodec("tradeable", Codec.BOOL).forGetter(GlobalEnchantmentFields::tradeable),
            EnchantmentConfigCodecs.defaultableCodec("treasure", Codec.BOOL).forGetter(GlobalEnchantmentFields::treasure)
    ).apply(inst, GlobalEnchantmentFields::new));

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
    public GlobalEnchantmentFields merge(GlobalEnchantmentFields oldConfiguration, Optional<GlobalEnchantmentFields> globalConfiguration) {
        Optional<Integer> maxLevel = MergeUtil.mergePrimitiveOptional(maxLevel(), oldConfiguration.maxLevel(), globalConfiguration.flatMap(GlobalEnchantmentFields::maxLevel));

        Map<Integer, VariableField<Integer>> effectivenessOverrides = MergeUtil.mergeMap(effectivenessOverrides(), oldConfiguration.effectivenessOverrides(), globalConfiguration.map(GlobalEnchantmentFields::effectivenessOverrides));

        Optional<List<HolderSet<Enchantment>>> incompatibilities = MergeUtil.mergeOptionalList(incompatibilities(), oldConfiguration.incompatibilities(), globalConfiguration.flatMap(GlobalEnchantmentFields::incompatibilities));

        Map<ItemAndTagMix, Integer> enchantingTableWeight = MergeUtil.mergeMap(enchantingTableWeight(), oldConfiguration.enchantingTableWeight(), globalConfiguration.map(GlobalEnchantmentFields::enchantingTableWeight));

        Optional<Boolean> tradeable = MergeUtil.mergePrimitiveOptional(tradeable(), oldConfiguration.tradeable(), globalConfiguration.flatMap(GlobalEnchantmentFields::tradeable));
        Optional<Boolean> treasure = MergeUtil.mergePrimitiveOptional(treasure(), oldConfiguration.treasure(), globalConfiguration.flatMap(GlobalEnchantmentFields::treasure));

        return new GlobalEnchantmentFields(maxLevel, effectivenessOverrides, incompatibilities, enchantingTableWeight, tradeable, treasure);
    }
}
