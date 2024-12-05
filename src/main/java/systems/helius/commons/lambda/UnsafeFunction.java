package systems.helius.commons.lambda;

import systems.helius.commons.exceptions.UnsafeCallRuntimeException;

import java.util.function.Function;

/**
 * A function which may throw an exception.
 * Used as compatibility to pass such functions to something that expects a regular Function.
 * @param <T> parameter type of the function
 * @param <R> return type of the function
 */
@FunctionalInterface
public interface UnsafeFunction<T, R> extends Function<T, R> {
    R invoke(T t) throws Throwable;

    @Override
    default R apply(T t) {
        try {
            return invoke(t);
        } catch (Throwable e) {
            throw new UnsafeCallRuntimeException(e);
        }
    }
}
