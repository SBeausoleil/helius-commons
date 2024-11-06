package systems.helius.commons.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MapUtils {
    private MapUtils() {}

    public static <K, V> void putIntoMultiMap(K key, V value, Map<K, List<V>> map) {
        List<V> values = map.computeIfAbsent(key, k -> new LinkedList<V>());
        values.add(value);
    }
}
