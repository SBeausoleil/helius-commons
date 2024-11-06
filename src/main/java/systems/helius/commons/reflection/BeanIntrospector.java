package systems.helius.commons.reflection;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BeanIntrospector {
    private BeanIntrospector() {}

    public static <T> Set<T> seekInstancesOfIn(Class<T> targetType, Object root, MethodHandles.Lookup context) {
        if (root == null) {
            return Collections.emptySet();
        }
        // TODO add support for when the targetType is null as if it was a lookout for null pointers
        Set<T> result = new HashSet<>();
        //seekInstancesOfInRecursive(targetType, root, context, , );
        return result;
    }

    private static <T> void seekInstancesOfInRecursive(Class<T> targetType, Object current, MethodHandles.Lookup context,
                                                       Set<T> result, Set<Object> visited) {
        for (Field field : ClassIntrospector.getAllFieldsFlat(current.getClass())) {
            field.setAccessible(true);
        }
    }
}
