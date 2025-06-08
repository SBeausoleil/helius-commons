package systems.helius.commons.exceptions;

import systems.helius.commons.reflection.TracedAccessException;

/**
 * An exception thrown when an introspection has a fatal failure.
 */
public class IntrospectionException extends IllegalAccessException {
    public IntrospectionException(TracedAccessException cause) {
        super(cause.buildPath());
        setStackTrace(cause.getStackTrace());
    }
}
