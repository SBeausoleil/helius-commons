package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;
import systems.helius.commons.collections.BridgingIterator;
import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;
import systems.helius.commons.reflection.SyntheticPrimitiveFields;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterativeAccessor implements ContentAccessor {
    @Override
    public boolean accepts(Object current, @Nullable Field holdingField, IntrospectionSettings settings) {
        return (current instanceof Iterable<?> && !settings.isDetailledIterableCheck())
                || (current instanceof Map<?, ?> && !settings.isDetailledMapCheck())
                || current.getClass().isArray();
    }

    @Override
    public <T> Stream<Content> extract(Object current, @Nullable Field holdingField, IntrospectionContext<T> context, IntrospectionSettings settings) {
        Stream<?> source;
        if (current.getClass().isArray()) {
            if (current.getClass().getComponentType().isPrimitive()) {
                final int LENGTH = Array.getLength(current);
                ArrayList<Object> list = new ArrayList<>(LENGTH);
                for (int i = 0; i < LENGTH; i++)
                    list.add(Array.get(current, i));
                source = list.stream();
                holdingField = SyntheticPrimitiveFields.getSyntheticPrimitiveField(current.getClass().getComponentType());
            } else {
                source = Arrays.stream((Object[]) current);
            }
        } else if (current instanceof Iterable<?> it) {
            source = StreamSupport.stream(it.spliterator(), false);
        } else if (current instanceof Map<?, ?> map) {
            int size = map.size() * 2;
            int characteristics = (map.keySet().spliterator().characteristics() & map.values().spliterator().characteristics())
                    | Spliterator.SIZED;
            var split = Spliterators.spliterator(new BridgingIterator(map.keySet(), map.values()), size, characteristics);
            source = StreamSupport.stream(split, false);
        } else {
            throw new UnsupportedOperationException("Type " + current.getClass() + " is not supported by the iterable scenario.");
        }
        final Field resultingField = holdingField;
        return source.map(value -> new Content(value, resultingField));
    }
}
