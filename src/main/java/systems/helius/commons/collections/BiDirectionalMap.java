package systems.helius.commons.collections;

import systems.helius.commons.annotations.Internal;
import systems.helius.commons.annotations.Unstable;

import java.util.*;

/**
 * A simple BiDirectionalMap implementation.
 * Unlike a traditional map, both keys and values are unique.
 * Not inherently thread-safe for writing.
 * @param <K>
 * @param <T>
 */
public class BiDirectionalMap<K, T> implements Map<K, T> {
    protected Map<K, T> keyToValue;
    protected Map<T, K> valueToKey;

    public BiDirectionalMap() {
        this.keyToValue = new HashMap<>();
        this.valueToKey = new HashMap<>();
    }

    // Not sure, as this could allow keeping control of the maps externally and altering them,
    // thus rendering this map in an invalid state
    @Internal
    public BiDirectionalMap(Map<K, T> keyMap, Map<T, K> valueMap) {
        if (!keyMap.isEmpty())
            throw new IllegalArgumentException("keyMap must be empty");
        if (!valueMap.isEmpty())
            throw new IllegalArgumentException("valueMap must be empty");
        this.keyToValue = keyMap;
        this.valueToKey = valueMap;
    }

    @Override
    public int size() {
        return keyToValue.size();
    }

    @Override
    public boolean isEmpty() {
        return keyToValue.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyToValue.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        //noinspection SuspiciousMethodCalls
        return valueToKey.containsKey(value);
    }

    @Override
    public T get(Object key) {
        return keyToValue.get(key);
    }

    @Override
    public T put(K key, T value) {
        T original = keyToValue.put(key, value);
        valueToKey.put(value, key);
        return original;
    }

    @Override
    public T remove(Object key) {
        T original = keyToValue.remove(key);
        if (original != null)
            valueToKey.remove(original);
        return original;
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        for (Map.Entry<? extends K, ? extends T> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        keyToValue.clear();
        valueToKey.clear();
    }

    @Override
    public Set<K> keySet() {
        return keyToValue.keySet();
    }

    @Override
    public Collection<T> values() {
        return valueToKey.keySet();
    }

    @Override
    public Set<Entry<K, T>> entrySet() {
        return keyToValue.entrySet();
    }
}
