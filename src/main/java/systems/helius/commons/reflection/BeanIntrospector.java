package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.collections.BridgingIterator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.lang.invoke.MethodHandles.Lookup;

public class BeanIntrospector {
    protected final IntrospectionSettings defaults;
    protected final ClassInspector classInspector;

    // IMPROVEMENT a map of fields and varhandles that are already known to resolve them?

    public BeanIntrospector() {
        this(null, null);
    }

    public BeanIntrospector(IntrospectionSettings defaults) {
        this(defaults, null);
    }

    public BeanIntrospector(ClassInspector classInspector) {
        this(null, classInspector);
    }

    public BeanIntrospector(@Nullable IntrospectionSettings defaults, @Nullable ClassInspector classInspector) {
        this.defaults = Objects.requireNonNullElseGet(defaults, IntrospectionSettings::new);
        this.classInspector = Objects.requireNonNullElseGet(classInspector, CachingClassInspector::new);
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

    /**
     * Seek within the root and all children for instances of a given type.
     * Warning! The returned set uses object identity (==), not equals() as is usually the case with sets.
     * @param targetType instances to find must be of that type or a covalent type.
     * @param root seek into
     * @param context the context of the caller. Should always be MethodHandles.lookup();
     * @return every instance found of the given type
     * @throws IllegalAccessException if any access right issue is found.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/IdentityHashMap.html">Java 17 API: IdentitHashMap</a>
     */
    public <T> Set<T> seek(Class<T> targetType, Object root, Lookup context/*, IntrospectionSettings introspectionOverrides TODO*/) throws IllegalAccessException {
        Set<T> found = Collections.newSetFromMap(new IdentityHashMap<>());
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
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

    protected <T> void depthFirstSearch(Class<T> targetType, Object current, @Nullable Field holdingField,
                                        Lookup rootContext, Lookup parent,
                                        IntrospectionSettings settings,
                                        Set<T> found, Set<Object> visited,
                                        int depth) throws TracedAccessException {
        if (current == null || depth >= settings.getMaxDepth() || visited.contains(current))
            return;
        visited.add(current);
        depth++;


        if (ClassInspector.evaluateTypingMatch(targetType, current, (holdingField != null ? holdingField.getType() : null))) {
            //noinspection unchecked covered by the static isAssignableFrom
            found.add((T) current);
            if (!settings.isEnterTargetType())
                return;
        }

        if (ClassInspector.isPrimitiveWrapper(current.getClass())) // Also catches primitives due to the autoboxing
            return;

        if ((current instanceof Iterable<?> && !settings.isDetailledIterableCheck())
                || (current instanceof Map<?, ?> && !settings.isDetailledMapCheck())
            || current.getClass().isArray()) {
            iterativeScenario(targetType, rootContext, settings, found, visited, depth, current, parent, holdingField);
        } else {
            try {
                Lookup lookup = classInspector.getPrivilegedLookup(current.getClass(), rootContext, parent, false);
                detailedInspectionScenario(targetType, current, rootContext, settings, found, visited, depth, lookup, holdingField);
            } catch (TracedAccessException e) {
                if (!settings.useSafeAccessCheck()) {
                    e.addStep(holdingField);
                    throw e;
                }
            }
        }
    }

    protected <T> void detailedInspectionScenario(Class<T> targetType, Object current,
                                                  Lookup rootContext, IntrospectionSettings settings,
                                                  Set<T> found, Set<Object> visited, int depth,
                                                  Lookup currentPrivilegedLookup, Field holdingField) throws TracedAccessException {

        // TODO replace this with a call to ClassInspector.getAllFieldsHandles
        Map<Class<?>, List<Field>> fields = classInspector.getAllFieldsHierarchical(current.getClass());
        if (fields.isEmpty()) return;

        for (Map.Entry<Class<?>, List<Field>> entry : fields.entrySet()) {
            if (currentPrivilegedLookup.lookupClass() != entry.getKey()) {
                // This grants access to the private fields within superclasses
                try {
                    currentPrivilegedLookup = classInspector.getPrivilegedLookup(entry.getKey(), currentPrivilegedLookup, rootContext, true);
                } catch (TracedAccessException e) {
                    if (!settings.useSafeAccessCheck()) {
                        e.addStep(holdingField);
                        throw e;
                    }
                    continue;
                }
            }
            for (Field field : entry.getValue()) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

                Object value;
                try {
                    value = currentPrivilegedLookup.unreflectVarHandle(field).get(current);
                } catch (IllegalAccessException e) {
                    // Shouldn't happen as we are supposed to have a private level of access into the class...
                    if (!settings.isIgnoreIllegalAccessError()) {
                        throw new TracedAccessException("Couldn't read the value of the field: " + field
                                + ". This should be impossible. " +
                                "Please file an issue at https://github.com/SBeausoleil/helius-commons/issues" +
                                " describing how this happened.", true, e);
                    }
                    continue;
                }
                if (value != null) {
                    try {
                        depthFirstSearch(targetType, value, field, rootContext, currentPrivilegedLookup, settings, found, visited, depth + 1);
                    } catch (TracedAccessException e) {
                        e.addStep(field);
                        throw e;
                    }
                }
            }
        }
    }


    /*
    Suppressing the warnings is required, as we are intentionally polluting the heap in the map scenario.
    Our pollution here is safe, as the polluted memory of the raw BridgingIterator never leaves the context
    of this method and we never do any operation within this method that operates on assumptions of the typing
    of the iterator since itself announces itself as a wildcard iterator.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> void iterativeScenario(Class<T> targetType, Lookup rootContext, IntrospectionSettings settings, Set<T> found, Set<Object> visited, int depth, Object value, Lookup lookup, Field holdingField) throws TracedAccessException {
        Iterator<?> it;
        if (value.getClass().isArray()) {
            if (value.getClass().getComponentType().isPrimitive()) {
                final int LENGTH = Array.getLength(value);
                ArrayList<Object> list = new ArrayList<>(LENGTH);
                for (int i = 0; i < LENGTH; i++)
                    list.add(Array.get(value, i));
                it = list.iterator();
                holdingField = SyntheticPrimitiveFields.getSyntheticPrimitiveField(value.getClass().getComponentType());
            } else {
                it = Arrays.asList((Object[]) value).iterator();
            }
        } else if (value instanceof Iterable<?> i) {
            it = i.iterator();
        } else if (value instanceof Map<?, ?> map) {
            it = new BridgingIterator(map.keySet(), map.values());
        } else {
            throw new UnsupportedOperationException("Type " + value.getClass() + " is not supported by the iterable scenario.");
        }
        while (it.hasNext()) {
            depthFirstSearch(targetType, it.next(), holdingField, rootContext, lookup, settings, found, visited, depth + 1);
        }
    }
}
