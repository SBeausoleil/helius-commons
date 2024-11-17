package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.invoke.MethodHandles.Lookup;

public class BeanIntrospector {
    protected IntrospectionSettings defaults;
    protected Map<Class<?>, MethodHandles.Lookup> privilegedLookups = new HashMap<>();

    public BeanIntrospector() {
        defaults = new IntrospectionSettings();
    }

    public BeanIntrospector(IntrospectionSettings defaults) {
        this.defaults = defaults;
    }

    public static <T> Set<T> seekIn(Class<T> targetType, Object root, Lookup context)
            throws IllegalAccessException {
        if (root == null) {
            return Collections.emptySet();
        }
        Set<T> found = Collections.newSetFromMap(new WeakHashMap<>());
        seekRecursively(targetType, root, context, found, Collections.newSetFromMap(new WeakHashMap<>()));
        return found;
    }

    private static <T> void seekRecursively(Class<T> targetType, Object current, Lookup context,
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

    /*
    Introspection algorithm:
    - 0: Add current object to "visited" set
    - 1: get all fields of current object
    - 2: for each field:
        - 2.1: Check if there is a special way to handle it (full control)
        - 2.2: Attempt to make it accessible:
            - 2.2.1: If that fails: look for a getter of said field
            - 2.2.2: If fails again: if SKIP_INACCESSIBLE: go to next iteration
            - 2.2.3: Else rethrow exception
        - 2.4: Get the value (if the value is NULL, go to next iteration)
        - 2.5: If the value is of the desired type:
            - 2.5.1: Add it to the sought list
            - 2.5.2: If NO_TARGET_INTROSPECTION: go to next iteration
        - 2.6: Enter the value for introspection
    - 3: Once no more fields: return (go back one level)
     */
    public <T> Set<T> seek(Class<T> targetType, Object root, Lookup context/*, IntrospectionSettings introspectionOverrides TODO*/) throws IllegalAccessException {
        Set<T> found = Collections.newSetFromMap(new WeakHashMap<>());
        Set<Object> visited = Collections.newSetFromMap(new WeakHashMap<>());
        try {
            depthFirstSearch(targetType, root, null, context, context, defaults, found, visited, 0);
        } catch (TracedAccessException e) {
            e.setRoot(root);
            if (e.isException())
                throw e.toIllegalAccessException();
            throw e.toIllegalAccessError();
        }
        return found;
    }

    private <T> void depthFirstSearch(Class<T> targetType, Object current, @Nullable Field holdingField,
                                      Lookup rootContext, Lookup parent,
                                      IntrospectionSettings settings,
                                      Set<T> found, Set<Object> visited,
                                      int depth) throws TracedAccessException {
        if (current == null  || depth >= settings.maxDepth || visited.contains(current))
            return;
        visited.add(current);
        depth++;


        if (evaluateTypingMatch(targetType, current, holdingField)) {
            //noinspection unchecked covered by the static isAssignableFrom
            found.add((T) current);
            if (!settings.enterTargetType)
                return;
        }

        if (current.getClass().isPrimitive())
            return;

        Lookup lookup = getPrivilegedLookup(current.getClass(), rootContext, parent);

        if ((current instanceof Iterable<?> && !settings.detailledIterableCheck)
                || (current instanceof Map<?, ?> && !settings.detailledMapCheck)) {
            iterativeScenario(targetType, rootContext, settings, found, visited, depth, current, lookup, holdingField);
        } else {
            singleValueScenario(targetType, current, rootContext, settings, found, visited, depth, lookup);
        }
    }

    private <T> void singleValueScenario(Class<T> targetType, Object current,
                                         Lookup rootContext, IntrospectionSettings settings,
                                         Set<T> found, Set<Object> visited, int depth,
                                         Lookup currentPrivilegedLookup) throws TracedAccessException {
        List<Field> fields = ClassIntrospector.getAllFieldsFlat(current.getClass());
        if (fields.isEmpty()) return;

        for (Field field : fields) {
            Object value;
            try {
                //value = currentPrivilegedLookup.unreflectVarHandle(field).get(current);
                value = currentPrivilegedLookup.unreflectVarHandle(field).get(current);
            } catch (IllegalAccessException e) {
                // Shouldn't happen as we are supposed to have a private level of access into the class...
                throw new TracedAccessException("Couldn't read the value of the field: " + field
                        + ". This really shouldn't happen. " +
                        "Please file an issue at https://github.com/SBeausoleil/helius-commons/issues describing how this happened.", e);
            }
            if (value != null) {
                try {
                    if ((value instanceof Iterable<?> && !settings.detailledIterableCheck)
                            || (value instanceof Map<?, ?> && !settings.detailledMapCheck)) {
                        iterativeScenario(targetType, rootContext, settings, found, visited, depth, value, currentPrivilegedLookup, field);
                    } else {
                        depthFirstSearch(targetType, value, field, rootContext, currentPrivilegedLookup, settings, found, visited, depth + 1);
                    }
                } catch (TracedAccessException e) {
                    e.addStep(field);
                }
            }
        }
    }

    private <T> void iterativeScenario(Class<T> targetType, Lookup rootContext, IntrospectionSettings settings, Set<T> found, Set<Object> visited, int depth, Object value, Lookup lookup, Field holdingField) throws TracedAccessException {
        Iterator<?> it;
        if (value instanceof Iterable<?> i) {
            it = i.iterator();
        } else {
            it = ((Map<?, ?>) value).entrySet().iterator();
        }
        while (it.hasNext()) {
            depthFirstSearch(targetType, it.next(), holdingField, rootContext, lookup, settings, found, visited, depth + 1);
        }
    }

    /**
     * Attempts to get a privileged lookup on a target class.
     *
     * @param type
     * @param rootContext
     * @param parent
     * @return
     */
    protected MethodHandles.Lookup getPrivilegedLookup(Class<?> type, MethodHandles.Lookup rootContext, MethodHandles.Lookup parent) throws TracedAccessException {
        MethodHandles.Lookup acquiredAccess = privilegedLookups.get(type);
        if (acquiredAccess != null)
            return acquiredAccess;

        try { // Check if the direct parent has access
            acquiredAccess = MethodHandles.privateLookupIn(type, parent);
        } catch (IllegalAccessException parentException) {
            try { // Fallback on the root context: perhaps the parent is part of a library who is not allowed such privileges
                acquiredAccess = MethodHandles.privateLookupIn(type, rootContext);
            } catch (IllegalAccessException rootContextException) {
                try { // Last resort: maybe this library is afforded the privilege by the type's module.
                    acquiredAccess = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
                } catch (IllegalAccessException libraryLookupException) {
                    // TODO In case of final failure, attempt to find an accessible getter method
                    throw new TracedAccessException("Couldn't get privileged lookup access into: " + type.getCanonicalName()
                            + ".\n Parent class: " + parentException.getMessage()
                            + ",\n root context: " + rootContextException.getMessage()
                            + ",\n library context: " + libraryLookupException.getMessage());
                }
            }
        }
        this.privilegedLookups.put(type, acquiredAccess);
        return acquiredAccess;
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
                switch (holdingField.getType().getName()) {
                    case "boolean":
                        return value.getClass() == Boolean.class;
                    case "byte":
                        return value.getClass() == Byte.class;
                    case "char":
                        return value.getClass() == Character.class;
                    case "short":
                        return value.getClass() == Short.class;
                    case "int":
                        return value.getClass() == Integer.class;
                    case "long":
                        return value.getClass() == Long.class;
                    case "float":
                        return value.getClass() == Float.class;
                    case "double":
                        return value.getClass() == Double.class;
                }
            } else if (Iterable.class.isAssignableFrom(holdingField.getType())) {
                // TODO handle
            }
        }
        return targetType.isAssignableFrom(value.getClass());
    }
}
