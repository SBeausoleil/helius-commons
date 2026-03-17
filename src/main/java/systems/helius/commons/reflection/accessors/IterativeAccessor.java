package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;
import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterativeAccessor implements ContentAccessor {
    @Override
    public boolean accepts(Object current, @Nullable Field holdingField, IntrospectionSettings settings) {
        return current instanceof Iterable<?>;
    }

    @Override
    public Collection<Content> extract(Object current, @Nullable Field holdingField, IntrospectionContext<?> context, IntrospectionSettings settings) {
        Iterable<?> it = (Iterable<?>) current;
        Stream<?> source = StreamSupport.stream(it.spliterator(), false);
        return source.map(value -> new Content(value, holdingField))
                .toList();
    }
}
