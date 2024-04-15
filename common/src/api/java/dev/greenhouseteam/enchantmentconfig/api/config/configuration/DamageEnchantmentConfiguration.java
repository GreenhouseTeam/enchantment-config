package dev.greenhouseteam.enchantmentconfig.api.config.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.codec.EnchantmentConfigCodecs;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import dev.greenhouseteam.enchantmentconfig.api.util.MergeUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DamageEnchantmentConfiguration(Optional<List<HolderSet<EntityType<?>>>> affectedEntities,
                                             Map<Integer, Float> damage) implements EnchantmentConfiguration {
    public static final Codec<DamageEnchantmentConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            EnchantmentConfigCodecs.defaultableCodec("affected_entities", Codec.list(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE.byNameCodec()))).forGetter(DamageEnchantmentConfiguration::affectedEntities),
            EnchantmentConfigCodecs.rangeAllowedIntegerCodec("level", "damage", Codec.FLOAT).fieldOf("damage").forGetter(DamageEnchantmentConfiguration::damage)
    ).apply(inst, DamageEnchantmentConfiguration::new));

    public float getBonusDamageAmount(int level, float original) {
        return EnchantmentConfigUtil.getFloatFromLevel(level, original, this.damage());
    }

    @Override
    public DamageEnchantmentConfiguration merge(EnchantmentConfiguration oldConfiguration) {
        DamageEnchantmentConfiguration castedOld = (DamageEnchantmentConfiguration) oldConfiguration;

        Optional<List<HolderSet<EntityType<?>>>> affectedEntities = MergeUtil.mergeOptionalList(this.affectedEntities(), castedOld.affectedEntities());
        Map<Integer, Float> damage = MergeUtil.mergeMap(this.damage(), castedOld.damage());

        return new DamageEnchantmentConfiguration(affectedEntities, damage);
    }
}
