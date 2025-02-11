package systems.helius.commons.reflection;

import systems.helius.commons.annotations.Internal;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Internal
public record IntrospectionContext<T>(Class<T> targetType,
                               MethodHandles.Lookup rootLookup,
                               Set<T> found,
                               Set<Object> visited) {
}
