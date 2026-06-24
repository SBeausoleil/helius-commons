package systems.helius.commons.chain;

import java.util.List;

/**
 * A ChainBuilder helps in the creation of responsibility chains.
 * <p>
 *     Elements are identified by a key to permit identifying elements that
 *     are lambdas and thus cannot be targeted by their type.
 * </p>
 *
 * @param <K> type of identifier of elements within the chain.
 * @param <T> type of elements in the chain that will do the actual processing.
 */
public interface ChainBuilder<K, T> {
    /**
     * Produces the actual processing chain.
     * @return a new chain instance.
     */
    List<T> build();

    /**
     * Add an element to the beginning of the chain.
     * @param key identifier of the
     * @param value
     * @return
     */
    ChainBuilder<K, T> addFirst(K key, T value);

    ChainBuilder<K, T> addLast(K key, T value);

    ChainBuilder<K, T> addBefore(K key, T value);

    ChainBuilder<K, T> addAfter(K key, T value);

    ChainBuilder<K, T> put(K key, T value);
}
