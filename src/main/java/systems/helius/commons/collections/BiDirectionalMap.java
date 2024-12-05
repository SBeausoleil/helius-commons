package systems.helius.commons.collections;


import java.util.*;
import java.util.function.Supplier;

/**
 * A simple BiDirectionalMap implementation.
 * Unlike a traditional map, both keys and values are unique.
 * Not inherently thread-safe for writing. If you need a thread-safe implementation, use one of the
 * constructors that allows you to provide the underlying Map implementation to provide a thread-safe
 * implementation, such as ConcurrentHashMap.
 *
 * @param <K> the type of keys
 * @param <T> the type of values
 */
public class BiDirectionalMap<K, T> implements Map<K, T> {
    protected Map<K, T> keyToValue;
    protected Map<T, K> valueToKey;

    public BiDirectionalMap() {
        this.keyToValue = new HashMap<>();
        this.valueToKey = new HashMap<>();
    }

    /**
     * Create a BiDirectionalMap whose backing maps are built by the provided Supplier
     * @param constructor provides new instances of the desired map implementation.
     */
    @SuppressWarnings("unchecked")
    public BiDirectionalMap(Supplier<? extends Map<?, ?>> constructor) {
        this.keyToValue = (Map<K, T>) constructor.get();
        this.valueToKey = (Map<T, K>) constructor.get();
    }

    /**
     * Create a BiDirectionalMap with the backing map implementations.
     *
     * @param keyMap   the map to use for the key-values
     * @param valueMap the map to use for the value-keys
     * @throws IllegalArgumentException if any of the argument map is not empty.
     */
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
