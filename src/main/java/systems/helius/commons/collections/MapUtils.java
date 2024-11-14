package systems.helius.commons.collections;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MapUtils {
    private MapUtils() {}

    public static <K, V> void putIntoMultiMap(Map<K, List<V>> map, K key, V value) {
        List<V> values = map.computeIfAbsent(key, k -> new LinkedList<V>());
        values.add(value);
    }
}
