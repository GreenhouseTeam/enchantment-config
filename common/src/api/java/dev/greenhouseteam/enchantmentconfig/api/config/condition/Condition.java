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

public interface Condition {
    Codec<Condition> CODEC = EnchantmentConfigRegistries.CONDITION_CODEC.byNameCodec().dispatch(condition -> condition.codec(), codec -> codec);

    boolean compare(EnchantmentType<?> enchantment);

    MapCodec<? extends Condition> codec();

    record And(List<Condition> conditions) implements Condition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("and");

        public static final MapCodec<And> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("conditions").forGetter(And::conditions)
        ).apply(inst, And::new));

        public boolean compare(EnchantmentType<?> enchantment) {
            return conditions().stream().allMatch(condition -> condition.compare(enchantment));
        }

        public MapCodec<And> codec() {
            return CODEC;
        }
    }

    record Or(List<Condition> conditions) implements Condition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("or");

        public static final MapCodec<Or> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("conditions").forGetter(Or::conditions)
        ).apply(inst, Or::new));

        public boolean compare(EnchantmentType<?> enchantment) {
            return conditions().stream().anyMatch(condition -> condition.compare(enchantment));
        }

        public MapCodec<Or> codec() {
            return CODEC;
        }
    }

    record Variable<I, O>(FieldPair<I, O> fieldPair, Comparison comparison) implements Condition {
        public static final ResourceLocation ID = EnchantmentConfigUtil.asResource("variable");

        public static final MapCodec<Variable<Object, Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                new FieldPair.Codec("value", "compare_to").xmap(pair -> {
                    if (pair.left().getInnerVariable() != null && !pair.left().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.left().getInnerVariable().getSerializer().id() + "' is unsupported.");
                    else if (pair.right().getInnerVariable() != null && !pair.right().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.right().getInnerVariable().getSerializer().id() + "' is unsupported.");
                    return pair;
                }, pair -> pair).forGetter(Variable::fieldPair),
                Comparison.CODEC.optionalFieldOf("comparison", Comparison.EQUAL).forGetter(Variable::comparison)
        ).apply(inst, Variable::new));

        public boolean compare(EnchantmentType<?> type) {
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(type.getEnchantment());
            O variable = fieldPair().left().get(enchantment, null, null);
            O value = fieldPair().right().get(enchantment, null, null);
            return comparison().function.apply(variable, value);
        }

        @Override
        public MapCodec<Variable<Object, Object>> codec() {
            return CODEC;
        }

    }
}
