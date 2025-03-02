package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.annotations.Unstable;
import systems.helius.commons.collections.BiDirectionalMap;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.*;

@Unstable
public sealed class ClassInspector permits CachingClassInspector {
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

    public ClassInspector() {
        // Preserve the empty constructor to guarantee future API compatibility
    }

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
    public Map<Class<?>, List<Field>> getAllFieldsHierarchical(Class<?> clazz) {
        var fields = new LinkedHashMap<Class<?>, List<Field>>();
        fields.put(clazz, List.of(clazz.getDeclaredFields()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && !superClass.equals(Enum.class)) {
            fields.putAll(getAllFieldsHierarchical(superClass));
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
    public List<Field> getAllFieldsFlat(Class<?> clazz) {
        Map<Class<?>, List<Field>> hierarchical = getAllFieldsHierarchical(clazz);
        int reserve = hierarchical.values().stream().mapToInt(List::size).sum();
        ArrayList<Field> buffer = new ArrayList<>(reserve);
        for (List<Field> fields : hierarchical.values()) {
            buffer.addAll(fields);
        }
        return buffer;
    }

    /**
     * Get all the fields and their private handle that are present in members of a given class.
     * @param clazz to analyze
     * @param context the context of the lookup
     * @return a map where the key is the field and the value its access handle.
     * @throws IllegalAccessException if the context is not allowed to access the field
     */
    public Map<Field, VarHandle> getAllFieldsHandles(Class<?> clazz, MethodHandles.Lookup context) throws IllegalAccessException {
        Map<Field, VarHandle> handles = new LinkedHashMap<>();
        var inspector = new ClassInspector();
        MethodHandles.Lookup privilegedLookup = context;
        for (Map.Entry<Class<?>, List<Field>> fieldsByClass :  getAllFieldsHierarchical(clazz).entrySet()) {
            if (context.lookupClass() != fieldsByClass.getKey()) {
                // This grants access to the private fields within superclasses
                try {
                    privilegedLookup = inspector.getPrivilegedLookup(fieldsByClass.getKey(), privilegedLookup, context, true);
                } catch (TracedAccessException e) {
                    throw new IllegalAccessException("Couldn't get private access to the class: " + fieldsByClass.getKey().getCanonicalName() + ". " + e.getMessage());
                }
            }
            for (Field field : fieldsByClass.getValue()) {
                handles.put(field, privilegedLookup.unreflectVarHandle(field));
            }
        }
        return handles;
    }

    /**
     *
     * @param targetType the sought type
     * @param value the object being checked
     * @param originalType Because of implicit casting rules in the Java Language, primitives are implicitly converted
     *                     to their wrapper type when passed to a method that takes an Object. Passing the original
     *                     type of the field that held the value allows us to deduce the correct true type of the value.
     * @return true if the real type of the value is the target type or is a child of it.
     */
    public static boolean evaluateTypingMatch(Class<?> targetType, Object value, @Nullable Class<?> originalType) {
        if (originalType != null) {
            if (originalType == Void.class)
                return true;

            if (originalType.isPrimitive()) {
                return targetType == PRIMITIVE_WRAPPERS.get(value.getClass());
            }
        }
        return targetType.isAssignableFrom(value.getClass());
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return PRIMITIVE_WRAPPERS.containsKey(clazz);
    }

    /**
     * Attempts to get a privileged (private-level access) lookup on a target class.
     *
     * @param target        the class on which a privileged lookup is desired.
     * @param rootContext   the original context of the request. Will be used as a backup if the parent may not grant privileged-access.
     * @param parent        the lookup on the class that owns the field where the target is the type.
     * @param forSuperclass indicates that the lookup is being made for the superclass of the parent.
     * @return a privileged lookup.
     */
    protected MethodHandles.Lookup getPrivilegedLookup(Class<?> target, MethodHandles.Lookup rootContext, MethodHandles.Lookup parent, boolean forSuperclass) throws TracedAccessException {
        MethodHandles.Lookup acquiredAccess;
        try { // Check if the direct parent has access
            acquiredAccess = MethodHandles.privateLookupIn(target, parent);
        } catch (IllegalAccessException | SecurityException parentException) {
            try { // Fallback on the root context: perhaps the parent is part of a library who is not allowed such privileges
                acquiredAccess = MethodHandles.privateLookupIn(target, rootContext);
            } catch (IllegalAccessException | SecurityException rootContextException) {
                try { // Last resort: maybe this library is afforded the privilege by the type's module.
                    acquiredAccess = MethodHandles.privateLookupIn(target, MethodHandles.lookup());
                } catch (IllegalAccessException | SecurityException libraryLookupException) {
                    throw new TracedAccessException("Couldn't get privileged lookup access into: " + target.getCanonicalName()
                            + (forSuperclass ? "\n Accessing superclass of: " + parent.lookupClass()
                            : ".\n Parent class: " + parentException.getMessage())
                            + ",\n root context: " + rootContextException.getMessage()
                            + ",\n library context: " + libraryLookupException.getMessage(),
                            false);
                }
            }
        }
        return acquiredAccess;
    }
}
