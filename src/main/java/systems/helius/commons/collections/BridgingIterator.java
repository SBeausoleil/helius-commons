package systems.helius.commons.collections;

import jakarta.annotation.Nullable;

import java.util.Iterator;

public class BridgingIterator<T> implements Iterator<T> {
    private final Iterable<T>[] sources;

    // Uses explicit elements to avoid varargs warnings
    public BridgingIterator(Iterable<T> i1, @Nullable Iterable<T> i2, @Nullable  Iterable<T> i3, @Nullable  Iterable<T> i4, @Nullable  Iterable<T> i5, @Nullable  Iterable<T> i6, @Nullable  Iterable<T> i7, @Nullable  Iterable<T> i8) {
        this.sources = sources;
    }
}
