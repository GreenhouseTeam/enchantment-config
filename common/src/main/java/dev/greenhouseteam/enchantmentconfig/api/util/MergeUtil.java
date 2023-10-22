package dev.greenhouseteam.enchantmentconfig.api.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MergeUtil {

    public static <T> T mergePrimitive(T currentValue, T oldValue, Optional<T> globalValue, int priority, int oldPriority, int globalPriority) {
        return globalValue.isPresent() && globalPriority > priority ? globalValue.get() : oldPriority > priority ? oldValue : currentValue;
    }

    /**
     * Creates a merged list of values present inside a current map,
     * old map, and global map.
     *
     * @param currentValue      The current list to merge with the old list
     * @param oldValue          The old map to use as a starting point.
     * @param globalValue       The global list to merge with the current list,
     *                          optional as it may not be present.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be lower than to
     *                          have the old value be used if it is present.
     * @param globalPriority    The value at which the priority must be lower than to
     *      *                   have the global value be used if it is present.
     *
     * @return                  An optional created from the value presented.
     *
     * @param <T>               The type parameter of the optional.
     */
    public static <T> Optional<T> mergePrimitiveOptional(Optional<T> currentValue, Optional<T> oldValue, Optional<T> globalValue, int priority, int oldPriority, int globalPriority) {
        return globalValue.isPresent() && globalPriority > priority ? globalValue : oldValue.isPresent() && oldPriority > priority ? oldValue : currentValue;
    }

    /**
     * Creates a merged list of values present inside a current map,
     * old map, and global map.
     *
     * @param currentList       The current list to merge with the old list
     * @param oldList           The old map to use as a starting point.
     * @param globalList        The global list to merge with the current list,
     *                          optional as it may not be present.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be higher than to
     *                          have the current value be merged if it is present.
     * @param globalPriority    The value at which the priority must be lower than to
     *                          have the global value be merged if it is present.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <T>               The type parameter of the list objects.
     */
    public static <T> List<T> mergeList(List<T> currentList, List<T> oldList, Optional<List<T>> globalList, int priority, int oldPriority, int globalPriority) {
        List<T> list = Lists.newArrayList();
        list.addAll(oldList);
        currentList.forEach((value) -> {
            if (!list.contains(value) || list.contains(value) && priority > oldPriority)
                list.add(value);
        });
        globalList.ifPresent(ls -> ls.forEach((value) -> {
            if (!list.contains(value) || list.contains(value) && globalPriority > priority)
                list.add(value);
        }));
        return ImmutableList.copyOf(list);
    }


    /**
     * Creates a merged optional list of values present inside an optional current map,
     * old map, and global map.
     *
     * @param currentList       The current list to merge with the old list
     * @param oldList           The old map to use as a starting point.
     * @param globalList        The global list to merge with the current list.
     * @param priority          The priority of the current merge.
     * @param oldPriority       The value at which the priority must be higher than to
     *                          have the current value be merged if it is present.
     * @param globalPriority    The value at which the priority must be lower than to
     *                          have the global value be merged if it is present.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <T>               The type parameter of the list objects.
     */
    public static <T> Optional<List<T>> mergeOptionalList(Optional<List<T>> currentList, Optional<List<T>> oldList, Optional<List<T>> globalList, int priority, int oldPriority, int globalPriority) {
        if (currentList.isEmpty() && oldList.isEmpty() && globalList.isEmpty())
            return Optional.empty();

        List<T> list = Lists.newArrayList();
        oldList.ifPresent(list::addAll);
        currentList.ifPresent(ls -> ls.forEach((value) -> {
            if (!list.contains(value) || list.contains(value) && priority > oldPriority)
                list.add(value);
        }));
        globalList.ifPresent(ls -> ls.forEach((value) -> {
            if (!list.contains(value) || list.contains(value) && globalPriority > priority)
                list.add(value);
        }));
        return Optional.of(ImmutableList.copyOf(list));
    }

    /**
     * Creates a map out of values present inside a current map,
     * old map, and global map.
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
        return ImmutableMap.copyOf(map);
    }

    /**
     * Creates an optional map out of values present inside an optional current map,
     * old map, and global map.
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
    public static <K, V> Optional<Map<K, V>> mergeOptionalMap(Optional<Map<K, V>> currentMap, Optional<Map<K, V>> oldMap, Optional<Map<K, V>> globalMap, int priority, int oldPriority, int globalPriority) {
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
        return Optional.of(ImmutableMap.copyOf(map));
    }

}
