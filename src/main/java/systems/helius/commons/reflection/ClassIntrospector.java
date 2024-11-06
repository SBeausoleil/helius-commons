package systems.helius.commons.reflection;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public final class ClassIntrospector {
    private static final WeakHashMap<Class<?>, Map<Class<?>, Field[]>> hierarchyCache = new WeakHashMap<>();
    private static final WeakHashMap<Class<?>, List<Field>> flatCache = new WeakHashMap<>();

    private ClassIntrospector() {}
    /**
     * Get all the fields that are present in members of a given class.
     * Recursively checks up into the class tree of clazz to accumulate members.
     *
     * @param clazz to analyze
     * @return all the fields that members of clazz have. This is in the form of a map where the key
     * the class of each superclass of the target class.
     */
    public static Map<Class<?>, Field[]> getAllFieldsHierarchical(Class<?> clazz) {
        Map<Class<?>, Field[]> fields = hierarchyCache.get(clazz);
        if (fields == null) {
            fields = new HashMap<>();
            fields.put(clazz, clazz.getDeclaredFields());
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                fields.putAll(getAllFieldsHierarchical(superClass));
            }
            hierarchyCache.put(clazz, fields);
        }
        return fields;
    }

    /**
     * Get all the fields that are present in members of a given class.
     * Recursively checks up into the class tree of clazz to accumulate members.
     *
     * @param clazz to analyze
     * @return all the fields that members of clazz have.
     */
    public static List<Field> getAllFieldsFlat(Class<?> clazz) {
        List<Field> result = flatCache.get(clazz);
        if (result == null) {
            Map<Class<?>, Field[]> fields = getAllFieldsHierarchical(clazz);
            int reserve = fields.values().stream().mapToInt(field -> field.length).sum();
            Field[] buffer = new Field[reserve];
            int index = 0;
            for (Field[] values : fields.values()) {
                System.arraycopy(values, 0, buffer, index, values.length);
                index += values.length;
            }
            result = Arrays.asList(buffer);
            flatCache.put(clazz, result);
        }
        return result;
    }

    /**
     * Get VarHandles for all the fields present in objects of a class,
     * including those that may not be directly accessible by the specific subclass.
     * @param clazz to analyze
     * @param lookup from your context.
     *               It is a simple as directly invoking {@link MethodHandles#lookup()} directly as the argument.
     *               This allows usage of your context's access rights and reduces IllegalAccessException likeliness.
     * @return a list of MethodHandle giving access to the fields within a class as if they were pure getters.
     * @throws IllegalAccessException
     */
    public static List<Accessor> getAllFieldAccessors(Class<?> clazz, MethodHandles.Lookup lookup) throws IllegalAccessException {
        List<Field> fields = getAllFieldsFlat(clazz);
        List<Accessor> result = new ArrayList<>(fields.size());
        for (Field field : fields) {
            field.setAccessible(true);
            MethodHandle getter = lookup.unreflectGetter(field);
            result.add(new Accessor(field, getter));
        }
        return result;
    }

    /**
     * Map all varhandles of a class in accordance to their full referenced type hierarchy.
     * @param clazz
     * @param lookup
     * @param includeComponentTypes if true, handles of collections/arrays that hold a type X
     *                              will be included in the list of handles that yield that type.
     *                                  No matter the value, collections will also be present as themselves.
     * @return
     * @throws IllegalAccessException
     */
    public static Map<Type, List<VarHandle>> getAllFieldHandlesByType(Class<?> clazz, MethodHandles.Lookup lookup,
                                                                      boolean includeComponentTypes) throws IllegalAccessException {
        var result = new HashMap<Type, List<VarHandle>>();

        return result;
    }
}
