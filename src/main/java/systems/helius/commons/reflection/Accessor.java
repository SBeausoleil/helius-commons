package systems.helius.commons.reflection;

import systems.helius.commons.lambda.UnsafeFunction;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 *
 * @param field no guarantee that the field is directly accessible or settable
 * @param getter provides access to the field's content. It may be a direct field access via virtual getter method
 *               or may instead transform how it's content is accessed.
 *               If multiple values are to be returned (like if the field designated an iterable value),
 *               the multiple values should be returned under in any implementation Iterable,
 *               but may also be returned as an iterator.
 */
public record Accessor(Field field, UnsafeFunction<Object, Object> getter) {
    public Class<?> getDeclaredType() {
        return field.getType();
    }

    public Object get(Object instance) {
        try {
            return getter.apply(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
