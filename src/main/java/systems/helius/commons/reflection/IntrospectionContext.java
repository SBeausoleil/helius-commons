package systems.helius.commons.reflection;

import systems.helius.commons.annotations.Internal;
import systems.helius.commons.reflection.accessors.AccessorsChain;
import systems.helius.commons.reflection.accessors.ContentAccessor;

import java.lang.invoke.MethodHandles;
import java.util.Set;

/**
 * Describes the constant elements that are referred to regularly during an introspection.
 * @param targetType
 * @param rootLookup
 * @param found
 * @param visited
 * @param <T> the target type
 */
@Internal
public record IntrospectionContext<T>(Class<T> targetType,
                                      MethodHandles.Lookup rootLookup,
                                      Set<T> found,
                                      Set<Object> visited,
                                      ContentAccessor contentAccessor) {
}
