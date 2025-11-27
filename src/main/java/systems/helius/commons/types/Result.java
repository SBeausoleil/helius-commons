package systems.helius.commons.types;

import jakarta.annotation.Nullable;
import systems.helius.commons.lambda.CheckedConsumer;
import systems.helius.commons.lambda.CheckedFunction;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A Result type that encapsulate either a value or an exception, depending upon the result of a function.
 *
 * @param <V> type of the value
 * @param <E> type of the exception
 */
public final class Result<V, E> {
    @Nullable
    private final V value;
    @Nullable
    private final E error;

    private Result(V value, E error) {
        this.value = value;
        this.error = error;
    }

    /**
     * Creates an successful result.
     *
     * @param value the value to return
     * @param <V>   type of the result
     * @param <E>   type of the exception, if it had happened
     * @return a successful result
     */
    public static <V, E> Result<V, E> ok(V value) {
        return new Result<>(value, null);
    }

    /**
     * Creates a failure result.
     *
     * @param error the exception to return
     * @param <V>   type of the result, if it had succeeded
     * @param <E>   type of the exception
     * @return a failed result
     */
    public static <V, E> Result<V, E> err(E error) {
        return new Result<>(null, error);
    }

    /**
     * Wrap a {@link java.util.function.Consumer} that can fail in a safe function that returns a Result instead of throwing.
     *
     * @param function the consumer to wrap
     * @param <T>      type consumed by the consumer
     * @param <E>      type of exception thrown by the consumer
     * @return a safe function
     */
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

    /**
     * Wrap a {@link Function} that can fail in a safe function that returns a Result instead of throwing.
     *
     * @param function the function to wrap
     * @param <T>
     * @param <V>
     * @param <E>
     * @return
     */
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

    public Optional<V> value() {
        return Optional.ofNullable(value);
    }

    public Optional<E> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Result<?, ?> result = (Result<?, ?>) o;
        return Objects.equals(value, result.value) && Objects.equals(error, result.error);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(value);
        result = 31 * result + Objects.hashCode(error);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                (value != null ? "value=" + value : "") +
                (error != null ? ", error=" + error : "") +
                '}';
    }
}
