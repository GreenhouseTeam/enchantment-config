package dev.greenhouseteam.enchantmentconfig.api.config.condition;

public class ComparisonUtil {
    private static final double EPSILON = 0.0001;

    public static boolean compareDouble(double o, double o2) {
        return Math.abs(o - o2) < EPSILON;
    }
}
