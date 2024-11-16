package systems.helius.commons.reflection;

/**
 * Special case handlers are allowed to modify the behavior of an introspector.
 */
public interface ValueHandler {
    boolean accepts(Object instance, Class<?> sought);
    HandlerResponse handle(IntrospectionContext context);
}
