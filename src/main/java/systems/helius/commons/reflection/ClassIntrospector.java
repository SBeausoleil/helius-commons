package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.collections.BiDirectionalMap;

import java.lang.reflect.Field;
import java.util.*;

public final class ClassIntrospector {
    private static final WeakHashMap<Class<?>, LinkedHashMap<Class<?>, Field[]>> hierarchyCache = new WeakHashMap<>();
    private static final WeakHashMap<Class<?>, List<Field>> flatCache = new WeakHashMap<>();

    /**
     * Wrapper types of Java lang primitives.
     * Key: Wrapper class
     * Value: primitive class
     */
    private static final BiDirectionalMap<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;
    static {
        PRIMITIVE_WRAPPERS = new BiDirectionalMap<>(new LinkedHashMap<>(), new LinkedHashMap<>());
        PRIMITIVE_WRAPPERS.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPERS.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPERS.put(Short.class, short.class);
        PRIMITIVE_WRAPPERS.put(Integer.class, int.class);
        PRIMITIVE_WRAPPERS.put(Long.class, long.class);
        PRIMITIVE_WRAPPERS.put(Float.class, float.class);
        PRIMITIVE_WRAPPERS.put(Double.class, double.class);
        PRIMITIVE_WRAPPERS.put(Character.class, char.class);
    }

    public ClassIntrospector() {}

    /**
     * Get all the fields that are present in members of a given class.
     * Recursively checks up into the class tree of clazz to accumulate members.
     * If iterated by entry, the first entry is always the clazz argument
     * and the last entry is always the top-most class in its hierarchy.
     * Object is ignored from the hierarchy.
     *
     * @param clazz to analyze
     * @return all the fields that members of clazz have. This is in the form of a map where the key
     * the class of each superclass of the target class.
     */
    public static LinkedHashMap<Class<?>, Field[]> getAllFieldsHierarchical(Class<?> clazz) {
        LinkedHashMap<Class<?>, Field[]> fields = hierarchyCache.get(clazz);
        if (fields == null) {
            fields = new LinkedHashMap<>();
            fields.put(clazz, clazz.getDeclaredFields());
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
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
     *
     * @param targetType the sought type
     * @param value the object being checked
     * @param holdingField Because of implicit casting rules in the Java Language, primitives are implicitly converted
     *                     to their wrapper type when passed to a method that takes an Object. Passing the field
     *                     that held the value allows us to deduce the correct true type of the value.
     * @return true if the real type of the value is the target type
     */
    public static boolean evaluateTypingMatch(Class<?> targetType, Object value, @Nullable Field holdingField) {
        if (holdingField != null) {
            if (holdingField.getType() == Void.class)
                return true;

            if (holdingField.getType().isPrimitive()) {
                return targetType == PRIMITIVE_WRAPPERS.get(value.getClass());
            } else if (Iterable.class.isAssignableFrom(holdingField.getType())) {
                // TODO handle
            }
        }
        return targetType.isAssignableFrom(value.getClass());
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return PRIMITIVE_WRAPPERS.containsKey(clazz);
    }
}
