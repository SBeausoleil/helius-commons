package systems.helius.commons.lambda;

import java.util.function.Function;

@FunctionalInterface
public interface UnsafeFunction<T, R> extends Function<T, R> {
    R invoke(T t) throws Throwable;

    @Override
    default R apply(T t) {
        try {
            return invoke(t);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
