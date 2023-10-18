package dev.greenhouseteam.enchantmentconfig.api.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public class MergeUtil {

    public static <T> T mergePrimitive(T currentValue, T oldValue, Optional<T> globalValue, int priority, int oldPriority, int globalPriority) {
        return globalValue.isPresent() && globalPriority > priority ? globalValue.get() : oldPriority > priority ? oldValue : currentValue;
    }

    public static <T> Optional<T> mergePrimitiveOptional(Optional<T> currentValue, Optional<T> oldValue, Optional<T> globalValue, int priority, int oldPriority, int globalPriority) {
        return globalValue.isPresent() && globalPriority > priority ? globalValue : currentValue.isEmpty() || oldPriority > priority ? oldValue : currentValue;
    }

    /**
     * Creates a map out of values present inside a current map,
     * an old map, and a global map.
     *
     * @param currentMap        The current map to merge with the old map.
     * @param oldMap            The old map to use as a starting point.
     * @param globalMap         The global map to merge with the current map,
     *                          optional as it may not be present.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be higher than to
     *                          have the current value be merged if it is present.
     * @param globalPriority    The value at which the priority must be lower than to
     *                          have the global value be merged if it is present.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <K>               The key type parameter of the map.
     * @param <V>               The value type parameter of the map.
     */
    public static <K, V> Map<K, V> mergeMap(Map<K, V> currentMap, Map<K, V> oldMap, Optional<Map<K, V>> globalMap, int priority, int oldPriority, int globalPriority) {
        Map<K, V> map = Maps.newHashMap();
        map.putAll(oldMap);
        currentMap.forEach((key, value) -> {
            if (!map.containsKey(key) || map.containsKey(key) && priority > oldPriority)
                map.put(key, value);
        });
        globalMap.ifPresent(kvMap -> kvMap.forEach((key, value) -> {
            if (!map.containsKey(key) || map.containsKey(key) && globalPriority > priority)
                map.put(key, value);
        }));
        return map;
    }

    /**
     * Creates an optional map out of values present inside a current map,
     * an old map, and a global map.
     *
     * @param currentMap        The current map to merge with the old map.
     * @param oldMap            The old map to use as a starting point.
     * @param globalMap         The global map to merge with the current map.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be higher than to
     *                          have the current value be merged if it is present.
     * @param globalPriority    The value at which the priority must be lower than to
     *                          have the global value be merged if it is present.
     *
     * @return                  An optional map with all the values of the other maps,
     *                          empty if all maps provided are empty.
     *
     * @param <K>               The key type parameter of the map.
     * @param <V>               The value type parameter of the map.
     */
    public static <K, V> Optional<Map<K, V>> mergeMapOptional(Optional<Map<K, V>> currentMap, Optional<Map<K, V>> oldMap, Optional<Map<K, V>> globalMap, int priority, int oldPriority, int globalPriority) {
        if (currentMap.isEmpty() && oldMap.isEmpty() && globalMap.isEmpty())
            return Optional.empty();

        Map<K, V> map = Maps.newHashMap();
        oldMap.ifPresent(map::putAll);
        currentMap.ifPresent(kvMap -> kvMap.forEach((key, value) -> {
            if (!map.containsKey(key) || map.containsKey(key) && priority > oldPriority)
                map.put(key, value);
        }));
        globalMap.ifPresent(kvMap -> kvMap.forEach((key, value) -> {
            if (!map.containsKey(key) || map.containsKey(key) && globalPriority > priority)
                map.put(key, value);
        }));
        return Optional.of(map);
    }

}
