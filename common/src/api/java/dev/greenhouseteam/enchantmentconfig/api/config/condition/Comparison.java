package dev.greenhouseteam.enchantmentconfig.api.config.condition;

import com.mojang.serialization.Codec;

import java.util.function.BiFunction;

public enum Comparison {
    EQUAL("==", (o, o2) -> {
        if (o instanceof Float || o instanceof Double) {
            return ComparisonUtil.compareDouble((double)o, (double)o2);
        }
        return o == o2;
    }),
    NOT_EQUAL("!=", (o, o2) -> {
        if (o instanceof Float || o instanceof Double) {
            return !ComparisonUtil.compareDouble((double)o, (double)o2);
        }
        return o != o2;
    }),
    GREATER(">", (o, o2) -> {
        if (o instanceof Number)
            return ((Number)o).doubleValue() > ((Number) o2).doubleValue();
        throw new UnsupportedOperationException("Could not compare non number object using > comparison.");
    }),
    LESSER("<", (o, o2) -> {
        if (o instanceof Number)
            return ((Number)o).doubleValue() < ((Number) o2).doubleValue();
        throw new UnsupportedOperationException("Could not compare non number object using < comparison.");
    }),
    GREATER_OR_EQUAL(">=", (o, o2) -> {
        if (o instanceof Number)
            return ((Number)o).doubleValue() >= ((Number) o2).doubleValue();
        throw new UnsupportedOperationException("Could not compare non number object using >= comparison.");
    }),
    LESSER_OR_EQUAL("<=", (o, o2) -> {
        if (o instanceof Number)
            return ((Number)o).doubleValue() <= ((Number) o2).doubleValue();
        throw new UnsupportedOperationException("Could not compare non number object using <= comparison.");
    });

    public static final Codec<Comparison> CODEC = Codec.STRING.xmap(Comparison::getFromString, comparison -> comparison.representation);

    final String representation;
    final BiFunction<Object, Object, Boolean>  function;

    Comparison(String representation, BiFunction<Object, Object, Boolean> function) {
        this.representation = representation;
        this.function = function;
    }

    public static Comparison getFromString(String string) {
        for (Comparison comparison : Comparison.values())
            if (comparison.representation.equals(string))
                return comparison;
        throw new RuntimeException("Could not get comparison type from '" + string + "'.");
    }

}
