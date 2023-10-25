package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DamageEnchantmentConfiguration(Optional<List<HolderSet<EntityType<?>>>> affectedEntities,
                                             Map<Integer, Float> damage) implements EnchantmentConfiguration {
    public static Codec<DamageEnchantmentConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("affected_entities", Codec.list(EnchantmentConfigCodecs.tagOrElementCodec(Registries.ENTITY_TYPE))).forGetter(DamageEnchantmentConfiguration::affectedEntities),
            EnchantmentConfigCodecs.rangeAllowedIntegerCodec("level", "damage", EnchantmentConfigCodecs.FLOAT).fieldOf("damage").forGetter(DamageEnchantmentConfiguration::damage)
    ).apply(inst, DamageEnchantmentConfiguration::new));

    public float getBonusDamageAmount(int level, float original) {
        return EnchantmentConfigUtil.getFloatFromLevel(level, original, this.damage());
    }

    @Override
    public DamageEnchantmentConfiguration merge(EnchantmentConfiguration oldConfiguration, Optional<EnchantmentConfiguration> globalConfiguration, int priority, int oldPriority, int globalPriority) {
        DamageEnchantmentConfiguration castedOld = (DamageEnchantmentConfiguration) oldConfiguration;
        Optional<DamageEnchantmentConfiguration> castedGlobal = globalConfiguration.map(c-> (DamageEnchantmentConfiguration)c);

        Optional<List<HolderSet<EntityType<?>>>> affectedEntities = MergeUtil.mergeOptionalList(this.affectedEntities(), castedOld.affectedEntities(), castedGlobal.flatMap(DamageEnchantmentConfiguration::affectedEntities), priority, oldPriority, globalPriority);
        Map<Integer, Float> damage = MergeUtil.mergeMap(this.damage(), castedOld.damage(), castedGlobal.map(DamageEnchantmentConfiguration::damage), priority, oldPriority, globalPriority);

        return new DamageEnchantmentConfiguration(affectedEntities, damage);
    }
}
