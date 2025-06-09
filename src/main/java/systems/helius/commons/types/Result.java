package systems.helius.commons.types;

import systems.helius.commons.lambda.CheckedConsumer;
import systems.helius.commons.lambda.CheckedFunction;

import java.util.Optional;
import java.util.function.Function;

public final class Result<V, E> {

    private final V value;
    private final E error;

    private Result(V value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <V, E> Result<V, E> ok(V value) {
        return new Result<>(value, null);
    }

    public static <V, E> Result<V, E> err(E error) {
        return new Result<>(null, error);
    }

    public static <T, E extends Exception> Function<T, Result<Void, E>> protectConsumer(CheckedConsumer<T, E> function) {
        return t -> {
            try {
                function.accept(t);
                return ok(null);
            } catch (Exception e) {
                try {
                    return err((E) e);
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException("The error type does not match the expected type", cce);
                }
            }
        };
    }

    public static <T, V, E extends Exception> Function<T, Result<V, E>> protect(CheckedFunction<T, V, E> function) {
        return t -> {
            try {
                return ok(function.apply(t));
            } catch (Exception e) {
                try {
                    return err((E) e);
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException("The error type does not match the expected type", cce);
                }
            }
        };
    }

    public boolean isOk() {
        return !isErr(); // Because of the scenario where the result type is Void, we can use this to check if the result is successful.
    }

    public boolean isErr() {
        return error != null;
    }

    public V getValue() {
        if (isOk()) {
            return value;
        }
        throw new IllegalStateException("Cannot get value from an error result");
    }

    public Optional<V> value() {
        return Optional.ofNullable(value);
    }

    public E getError() {
        if (isErr()) {
            return error;
        }
        throw new IllegalStateException("Cannot get error from a successful result");
    }

    public Optional<E> error() {
        return Optional.ofNullable(error);
    }
}
