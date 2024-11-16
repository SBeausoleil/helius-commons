package systems.helius.commons.reflection;


import java.lang.reflect.Field;

public interface FieldHandler {
    boolean accepts(Field field, Class<?> of);
    HandlerResponse handle(Field field, Class<?> of, Object value);
}
