package systems.helius.commons.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public record Accessor(Field field, MethodHandle getter) {
    public Class<?> getDeclaredType() {
        return field.getType();
    }

    public Object get(Object instance) {
        try {
            return getter.invoke(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
