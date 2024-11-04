package systems.helius.commons.reflection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BeanIntrospection {
    private BeanIntrospection() {}

    public static <T> Set<T> seekInstancesOfIn(Class<T> targetType, Object root) {
        if (root == null) {
            return Collections.emptySet();
        }
        // TODO add support for when the targetType is null as if it was a lookout for null pointers
        Set<T> result = new HashSet<>();
        seekInstancesOfInRecursive(targetType, root, result,  new HashSet<>());
        return result;
    }

    private static <T> void seekInstancesOfInRecursive(Class<T> targetType, Object current,
                                                       Set<T> result, Set<Object> visited) {
        for (Field field : ClassAnalyzer.getAllFieldsFlat(current.getClass())) {
            field.setAccessible(true);
        }
    }
}
