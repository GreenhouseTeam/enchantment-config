package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.util.EnchantmentConfigUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public interface EnchantmentCondition {
    Codec<EnchantmentCondition> CODEC = EnchantmentConfigRegistries.CONDITION_CODEC.byNameCodec().dispatch(EnchantmentCondition::getCodec, codec -> codec);

    boolean compare(EnchantmentType<?> enchantment);

    MapCodec<? extends EnchantmentCondition> getCodec();

    record And(List<EnchantmentCondition> conditions) implements EnchantmentCondition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("and");

        public static final MapCodec<And> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentCondition.CODEC.listOf().fieldOf("conditions").forGetter(And::conditions)
        ).apply(inst, And::new));

        public boolean compare(EnchantmentType<?> enchantment) {
            return conditions().stream().allMatch(condition -> condition.compare(enchantment));
        }

        public MapCodec<And> getCodec() {
            return CODEC;
        }
    }

    record Or(List<EnchantmentCondition> conditions) implements EnchantmentCondition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("or");

        public static final MapCodec<Or> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                EnchantmentCondition.CODEC.listOf().fieldOf("conditions").forGetter(Or::conditions)
        ).apply(inst, Or::new));

        public boolean compare(EnchantmentType<?> enchantment) {
            return conditions().stream().anyMatch(condition -> condition.compare(enchantment));
        }

        public MapCodec<Or> getCodec() {
            return CODEC;
        }
    }

    record Variable<T>(VariableAndCompareTo<T> variableAndCompareTo, Comparison comparison) implements EnchantmentCondition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("variable");

        public static final MapCodec<Variable<Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                VariableAndCompareTo.Codec.INSTANCE.forGetter(Variable::variableAndCompareTo),
                Comparison.CODEC.fieldOf("comparison").forGetter(Variable::comparison)
        ).apply(inst, Variable::new));

        public boolean compare(EnchantmentType<?> type) {
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
            T variable = variableAndCompareTo().variable().getValue(enchantment, null, null);
            T value = variableAndCompareTo().compareTo().get(enchantment, null, null);
            return comparison().function.apply(variable, value);
        }

        public MapCodec<Variable<Object>> getCodec() {
            return CODEC;
        }
    }
}
