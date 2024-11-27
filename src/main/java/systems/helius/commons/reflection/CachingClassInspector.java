package systems.helius.commons.reflection;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CachingClassInspector {
    private final Map<Class<?>, Map<Class<?>, List<Field>>> hierarchyCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Field>> flatCache = new ConcurrentHashMap<>();

    /**
     * Get all the fields that are present in members of a given class.
     * Recursively checks up into the class tree of clazz to accumulate members.
     * If iterated by entry, the first entry is always the clazz argument
     * and the last entry is always the top-most class in its hierarchy.
     * Object is ignored from the hierarchy.
     *
     * @param clazz to analyze
     * @return all the fields that members of clazz have. This is in the form of a map where the key
     * the class of each superclass of the target class. The map itself and its lists are unmodifiable.
     */
    public Map<Class<?>, List<Field>> getAllFieldsHierarchical(Class<?> clazz) {
        Map<Class<?>, List<Field>> result = hierarchyCache.get(clazz);
        if (result == null) {
            LinkedHashMap<Class<?>, Field[]> raw = ClassInspector.getAllFieldsHierarchical(clazz);
            result = new LinkedHashMap<>();
            for (Map.Entry<Class<?>, Field[]> entry : raw.entrySet()) {
                result.put(entry.getKey(), List.of(entry.getValue()));
            }
            hierarchyCache.put(clazz, Collections.unmodifiableMap(result));
        }
        return result;
    }

    /**
     * Get all the fields that are present in members of a given class.
     * Recursively checks up into the class tree of clazz to accumulate members.
     *
     * @param clazz to analyze
     * @return all the fields that members of clazz have.
     */
    public List<Field> getAllFieldsFlat(Class<?> clazz) {
        List<Field> result = flatCache.get(clazz);
        if (result == null) {
            result = List.copyOf(ClassInspector.getAllFieldsFlat(clazz));
            flatCache.put(clazz, result);
        }
        return result;
    }
}
