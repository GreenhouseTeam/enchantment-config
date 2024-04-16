package dev.greenhouseteam.enchantmentconfig.api.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MergeUtil {

    public static <T> T mergePrimitive(T currentValue, T oldValue) {
        return mergePrimitive(currentValue, oldValue, Optional.empty());
    }

    public static <T> T mergePrimitive(T currentValue, T oldValue, Optional<T> globalValue) {
        return mergePrimitiveOptional(Optional.of(currentValue), Optional.of(oldValue), globalValue).orElse(null);
    }

    public static <T> Optional<T> mergePrimitive(Optional<T> currentValue, Optional<T> oldValue) {
        return mergePrimitiveOptional(currentValue, oldValue, Optional.empty());
    }

    /**
     * Creates a merged list of values present inside a current map,
     * old map, and global map.
     *
     * @param currentValue      The current list to merge with the old list
     * @param oldValue          The old map to use as a starting point.
     * @param globalValue       The global list to merge with the current list,
     *
     * @return                  An optional created from the value presented.
     *
     * @param <T>               The type parameter of the optional.
     */
    public static <T> Optional<T> mergePrimitiveOptional(Optional<T> currentValue, Optional<T> oldValue, Optional<T> globalValue) {
        return currentValue.isPresent() ? currentValue : oldValue.isPresent() ? oldValue : globalValue;
    }

    public static <T> List<T> mergeList(List<T> currentList, Optional<List<T>> oldList) {
        return mergeList(currentList, oldList, Optional.empty());
    }

    /**
     * Creates a merged list of values present inside a current map,
     * old map, and global map.
     *
     * @param currentList       The current list to merge with the old list
     * @param oldList           The old map to use as a starting point.
     * @param globalList        The global list to merge with the current list,
     *                          optional as it may not be present.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <T>               The type parameter of the list objects.
     */
    public static <T> List<T> mergeList(List<T> currentList, Optional<List<T>> oldList, Optional<List<T>> globalList) {
        List<T> list = Lists.newArrayList();
        globalList.ifPresent(list::addAll);
        oldList.ifPresent(list::addAll);
        list.addAll(currentList);
        return ImmutableList.copyOf(list);
    }


    public static <T> Optional<List<T>> mergeOptionalList(Optional<List<T>> currentList, Optional<List<T>> oldList) {
        return mergeOptionalList(currentList, oldList, Optional.empty());
    }

    /**
     * Creates a merged optional list of values present inside an optional current map,
     * old map, and global map.
     *
     * @param currentList       The current list to merge with the old list
     * @param oldList           The old map to use as a starting point.
     * @param globalList        The global list to merge with the current list.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <T>               The type parameter of the list objects.
     */
    public static <T> Optional<List<T>> mergeOptionalList(Optional<List<T>> currentList, Optional<List<T>> oldList, Optional<List<T>> globalList) {
        if (currentList.isEmpty() && oldList.isEmpty() && globalList.isEmpty())
            return Optional.empty();

        List<T> list = Lists.newArrayList();
        globalList.ifPresent(list::addAll);
        oldList.ifPresent(list::addAll);
        currentList.ifPresent(list::addAll);
        return Optional.of(ImmutableList.copyOf(list));
    }

    public static <K, V> Map<K, V> mergeMap(Map<K, V> currentMap, Optional<Map<K, V>> oldMap) {
        return mergeMap(currentMap, oldMap, Optional.empty());
    }

    /**
     * Creates a map out of values present inside a current map,
     * old map, and global map.
     *
     * @param currentMap        The current map to merge with the old map.
     * @param oldMap            The old map to use as a starting point.
     * @param globalMap         The global map to merge with the current map,
     *                          optional as it may not be present.
     *
     * @return                  A map with all the values of the other maps.
     *
     * @param <K>               The key type parameter of the map.
     * @param <V>               The value type parameter of the map.
     */
    public static <K, V> Map<K, V> mergeMap(Map<K, V> currentMap, Optional<Map<K, V>> oldMap, Optional<Map<K, V>> globalMap) {
        Map<K, V> map = Maps.newHashMap();
        globalMap.ifPresent(map::putAll);
        oldMap.ifPresent(map::putAll);
        map.putAll(currentMap);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Optional<Map<K, V>> mergeOptionalMap(Optional<Map<K, V>> currentMap, Optional<Map<K, V>> oldMap) {
        return mergeOptionalMap(currentMap, oldMap, Optional.empty());
    }

    /**
     * Creates an optional map out of values present inside an optional current map,
     * old map, and global map.
     *
     * @param currentMap        The current map to merge with the old map.
     * @param oldMap            The old map to use as a starting point.
     * @param globalMap         The global map to merge with the current map.
     *
     * @return                  An optional map with all the values of the other maps,
     *                          empty if all maps provided are empty.
     *
     * @param <K>               The key type parameter of the map.
     * @param <V>               The value type parameter of the map.
     */
    public static <K, V> Optional<Map<K, V>> mergeOptionalMap(Optional<Map<K, V>> currentMap, Optional<Map<K, V>> oldMap, Optional<Map<K, V>> globalMap) {
        if (currentMap.isEmpty() && oldMap.isEmpty() && globalMap.isEmpty())
            return Optional.empty();

        Map<K, V> map = Maps.newHashMap();
        globalMap.ifPresent(map::putAll);
        oldMap.ifPresent(map::putAll);
        currentMap.ifPresent(map::putAll);
        return Optional.of(ImmutableMap.copyOf(map));
    }

}
