package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchantmentconfig.api.config.type.EnchantmentType;
import dev.greenhouseteam.enchantmentconfig.api.registries.EnchantmentConfigRegistries;
import dev.greenhouseteam.enchantmentconfig.api.EnchantmentConfigApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

public interface Condition {
    Codec<Condition> CODEC = Codec.withAlternative(EnchantmentConfigRegistries.CONDITION_CODEC.byNameCodec().dispatch(Condition::codec, codec -> codec), Variable.CODEC.codec());

    /**
     * Compares the EnchantmentType with this condition.
     *
     * @param enchantment   The EnchantmentType to compare.
     * @return              Whether the comparison was successful.
     */
    boolean compare(EnchantmentType<?> enchantment);

    /**
     * @return  The {@link MapCodec} associated with this condition.
     */
    MapCodec<? extends Condition> codec();

    /**
     * An And Condition class.
     * Used to store a count_if condition configured as if it were set up like an and condition.
     */
    class And {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("and");
        public static final MapCodec<CountIf> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("conditions").forGetter(CountIf::conditions)
        ).apply(inst, list -> new CountIf(list, Optional.empty(), Comparison.EQUAL)));
    }

    /**
     * An Or Condition class.
     * Used to store a count_if condition configured as if it were set up like an or condition.
     */
    class Or {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("or");
        public static final MapCodec<CountIf> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("conditions").forGetter(CountIf::conditions)
        ).apply(inst, list -> new CountIf(list, Optional.of(0), Comparison.GREATER)));
    }

    /**
     * A Xor Condition class.
     * Used to store a count_if condition configured as if it were set up like a xor condition.
     */
    class Xor {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("xor");
        public static final MapCodec<CountIf> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(2, 2).fieldOf("conditions").forGetter(CountIf::conditions)
        ).apply(inst, list -> new CountIf(list, Optional.of(1), Comparison.EQUAL, Optional.of(1), Optional.of(Comparison.EQUAL))));
    }

    /**
     * A condition that returns based on how many conditions have been successful, and a comparison.
     *
     * @param conditions            The conditions to check within this condition.
     * @param successes             The amount of successes to be compared to the comparison for this to pass.
     *                              If empty, defaults to the size of the conditions list.
     * @param comparison            The comparison to compare the amount of successes to.
     * @param failures              The amount of failures to be compared to the comparison for this to pass.
     *                              If empty, defaults to the required successes minus the size of the conditions list.
     * @param failureComparison     The comparison to compare the amount of failures to.
     *                              If empty, failures will not be checked.
     */
    record CountIf(List<Condition> conditions,
                   Optional<Integer> successes, Comparison comparison,
                   Optional<Integer> failures, Optional<Comparison> failureComparison) implements Condition {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("count_if");

        public static final MapCodec<CountIf> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Condition.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("conditions").forGetter(CountIf::conditions),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("successes").forGetter(CountIf::successes),
                Comparison.CODEC.optionalFieldOf("comparison", Comparison.EQUAL).forGetter(CountIf::comparison),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("failures").forGetter(CountIf::failures),
                Comparison.CODEC.optionalFieldOf("failure_comparison").forGetter(CountIf::failureComparison)
        ).apply(inst, CountIf::new));

        public CountIf(List<Condition> conditions, Optional<Integer> successes, Comparison comparison) {
            this(conditions, successes, comparison, Optional.empty(), Optional.empty());
        }

        public boolean compare(EnchantmentType<?> enchantment) {
            int currentSuccesses = 0;
            int currentFailures = 0;
            for (Condition condition : conditions) {
                if (condition.compare(enchantment))
                    ++currentSuccesses;
                else
                    ++currentFailures;
            }
            int requiredSuccesses = successes.orElseGet(conditions::size);
            boolean returnValue = comparison.compare(currentSuccesses, requiredSuccesses);
            if (failureComparison.isEmpty())
                return returnValue;

            int requiredFailures = failures.orElseGet(() -> conditions.size() - requiredSuccesses);
            returnValue = returnValue && failureComparison.get().compare(currentFailures, requiredFailures);
            return returnValue;
        }

        public MapCodec<CountIf> codec() {
            return CODEC;
        }
    }

    /**
     * An Not Condition class.
     * Used to store a variable condition configured as if it were set up like a not condition.
     */
    class Not {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("not");
        public static final MapCodec<Variable<Object, Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                new FieldPair.Codec("value", "compare_to").xmap(pair -> {
                    if (pair.left().getInnerVariable() != null && !pair.left().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.left().getInnerVariable().getSerializer().id() + "' is unsupported in conditions.");
                    else if (pair.right().getInnerVariable() != null && !pair.right().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.right().getInnerVariable().getSerializer().id() + "' is unsupported in conditions.");
                    return pair;
                }, pair -> pair).forGetter(Variable::fieldPair)
        ).apply(inst, (fieldPair) -> new Variable<>(fieldPair, Comparison.NOT_EQUAL)));
    }

    /**
     * A field based comparison.
     *
     * @param fieldPair     The field pair used within comparing values.
     * @param comparison    The comparison for the field pair.
     * @param <I>           The input of the field pair.
     * @param <O>           The output of the field pair.
     */
    record Variable<I, O>(FieldPair<I, O> fieldPair, Comparison comparison) implements Condition {
        public static final ResourceLocation ID = EnchantmentConfigApi.asResource("variable");

        public static final MapCodec<Variable<Object, Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                new FieldPair.Codec("value", "compare_to").xmap(pair -> {
                    if (pair.left().getInnerVariable() != null && !pair.left().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.left().getInnerVariable().getSerializer().id() + "' is unsupported in conditions.");
                    else if (pair.right().getInnerVariable() != null && !pair.right().getInnerVariable().allowedInRootCondition())
                        throw new UnsupportedOperationException("Variable of type '" + pair.right().getInnerVariable().getSerializer().id() + "' is unsupported in conditions.");
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
