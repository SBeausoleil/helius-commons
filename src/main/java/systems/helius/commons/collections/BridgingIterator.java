package systems.helius.commons.collections;

import jakarta.annotation.Nullable;

import java.util.*;

/**
 * An iterator that reads seamlessly through multiple iterables, hereafter named "sources".
 * Sources are lazily read. All method calls are delegated to the iterator of the currently read source.
 * If any of the sources have a fail-fast iterator, it will only fire if said source is currently being iterated upon;
 * if its reading is over or hasn't started, a source is safe to modify externally.
 * @param <T> the type of content within the iterables
 */
public class BridgingIterator<T> implements Iterator<T> {
    private final Iterable<T>[] sources;
    private int index;
    @Nullable
    private Iterator<T> currentSourceIt;
    @Nullable
    private Iterator<T> previousSourceIt;
    
    @SafeVarargs
    public BridgingIterator(Iterable<T>... sources) {
        this.sources = sources;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        if (currentSourceIt == null || !currentSourceIt.hasNext()) {
            Optional<Iterator<T>> nextIt = nextSegment();
            if (nextIt.isPresent()) {
                currentSourceIt = nextIt.get();
            } else {
                return false;
            }
        }
        return currentSourceIt.hasNext();
    }

    protected Optional<Iterator<T>> nextSegment() {
        previousSourceIt = currentSourceIt;
        if (index < sources.length) {
            return Optional.of(sources[index++].iterator());
        }
        return Optional.empty();
    }

    @Override
    public T next() {
        if (currentSourceIt == null || !currentSourceIt.hasNext()) {
            currentSourceIt = nextSegment().orElseThrow(NoSuchElementException::new);
        } else {
            previousSourceIt = null;
        }
        return currentSourceIt.next();
    }

    /**
     * Removes from the underlying collection the last element returned
     * by this iterator (optional operation).  This method can be called
     * only once per call to {@link #next}.
     * <p>
     * The behavior of the BridgingIterator regarding this method depends upon
     * that of the iterator of the current source being read.
     *
     * @throws UnsupportedOperationException if the {@code remove}
     *         operation is not supported by the current source.
     *
     * @throws IllegalStateException if the {@code next} method has not
     *         yet been called, or the {@code remove} method has already
     *         been called after the last call to the {@code next}
     *         method.
     */
    @Override
    public void remove() {
        if (currentSourceIt == null)
            throw new IllegalStateException("The next() method has not already been called.");
        Objects.requireNonNullElseGet(previousSourceIt, () -> currentSourceIt).remove();
    }

    /**
     * Return the source that is currently being read.
     * <p>
     * WARNING: modifying the returned source incurs a risk of heap-pollution!
     */
    public Iterable<T> currentSource() {
        return sources[index > 0 ? (index - 1) : 0];
    }
}
