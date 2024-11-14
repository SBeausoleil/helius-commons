package systems.helius.commons.reflection;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class BeanIntrospector {
    private BeanIntrospector() {}

    public static <T> Set<T> seekIn(Class<T> targetType, Object root, MethodHandles.Lookup context)
            throws IllegalAccessException {
        if (root == null) {
            return Collections.emptySet();
        }
        Set<T> found = Collections.newSetFromMap(new WeakHashMap<>());
        seekRecursively(targetType, root, context, found, Collections.newSetFromMap(new WeakHashMap<>()));
        return found;
    }

    private static <T> void seekRecursively(Class<T> targetType, Object current, MethodHandles.Lookup context,
                                            Set<T> result, Set<Object> visited) throws IllegalAccessException {
        if (visited.contains(current)) return;
        visited.add(current);
        for (Accessor accessor : ClassIntrospector.getAllFieldAccessors(current.getClass(), context)) {
            Object next = accessor.get(current);
            if (targetType.isAssignableFrom(accessor.getDeclaredType()) && next != null) {
                result.add((T) next);
            }
            seekRecursively(targetType, next, context, result, visited);
        }
    }
}
