package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.invoke.MethodHandles.Lookup;

public class BeanIntrospector {
    protected IntrospectionSettings defaults;
    protected Map<Class<?>, MethodHandles.Lookup> privilegedLookups = new HashMap<>();

    // IMPROVEMENT a map of fields and varhandles that are already known to resolve them?

    public BeanIntrospector() {
        defaults = new IntrospectionSettings();
    }

    public BeanIntrospector(IntrospectionSettings defaults) {
        this.defaults = defaults;
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
     * @param targetType
     * @param root
     * @param context
     * @return
     * @param <T>
     * @throws IllegalAccessException
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


        if (ClassIntrospector.evaluateTypingMatch(targetType, current, holdingField)) {
            //noinspection unchecked covered by the static isAssignableFrom
            found.add((T) current);
            if (!settings.isEnterTargetType())
                return;
        }

        if (current.getClass().isPrimitive() || ClassIntrospector.isPrimitiveWrapper(current.getClass()))
            return;

        if ((current instanceof Iterable<?> && !settings.isDetailledIterableCheck())
                || (current instanceof Map<?, ?> && !settings.isDetailledMapCheck())
            || current.getClass().isArray()) {
            iterativeScenario(targetType, rootContext, settings, found, visited, depth, current, parent, holdingField);
        } else {
            Lookup lookup = getPrivilegedLookup(current.getClass(), rootContext, parent, false);
            singularObjectScenario(targetType, current, rootContext, settings, found, visited, depth, lookup, holdingField);
        }
    }

    protected <T> void singularObjectScenario(Class<T> targetType, Object current,
                                              Lookup rootContext, IntrospectionSettings settings,
                                              Set<T> found, Set<Object> visited, int depth,
                                              Lookup currentPrivilegedLookup, Field holdingField) throws TracedAccessException {
        LinkedHashMap<Class<?>, Field[]> fields = ClassIntrospector.getAllFieldsHierarchical(current.getClass());
        if (fields.isEmpty()) return;

        for (Map.Entry<Class<?>, Field[]> entry : fields.entrySet()) {
            if (currentPrivilegedLookup.lookupClass() != entry.getKey()) {
                // This grants access to the private fields within superclasses
                try {
                    currentPrivilegedLookup = getPrivilegedLookup(entry.getKey(), currentPrivilegedLookup, rootContext, true);
                } catch (TracedAccessException e) {
                    e.addStep(holdingField);
                }
            }
            for (Field field : entry.getValue()) {
                Object value;
                try {
                    value = currentPrivilegedLookup.unreflectVarHandle(field).get(current);
                } catch (IllegalAccessException e) {
                    // Shouldn't happen as we are supposed to have a private level of access into the class...
                    if (!settings.isIgnoreIllegalAccessError()) {
                        throw new TracedAccessException("Couldn't read the value of the field: " + field
                                + ". This should be impossible. " +
                                "Please file an issue at https://github.com/SBeausoleil/helius-commons/issues" +
                                " describing how this happened.", e);
                    }
                    continue;
                }
                if (value != null) {
                    try {
                        depthFirstSearch(targetType, value, field, rootContext, currentPrivilegedLookup, settings, found, visited, depth + 1);
                    } catch (TracedAccessException e) {
                        e.addStep(field);
                    }
                }
            }
        }
    }

    protected <T> void iterativeScenario(Class<T> targetType, Lookup rootContext, IntrospectionSettings settings, Set<T> found, Set<Object> visited, int depth, Object value, Lookup lookup, Field holdingField) throws TracedAccessException {
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
        } else {
            it = ((Map<?, ?>) value).entrySet().iterator();
        }
        while (it.hasNext()) {
            depthFirstSearch(targetType, it.next(), holdingField, rootContext, lookup, settings, found, visited, depth + 1);
        }
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
        MethodHandles.Lookup acquiredAccess = privilegedLookups.get(target);
        if (acquiredAccess != null)
            return acquiredAccess;

        try { // Check if the direct parent has access
            acquiredAccess = MethodHandles.privateLookupIn(target, parent);
        } catch (IllegalAccessException | SecurityException parentException) {
            try { // Fallback on the root context: perhaps the parent is part of a library who is not allowed such privileges
                acquiredAccess = MethodHandles.privateLookupIn(target, rootContext);
            } catch (IllegalAccessException | SecurityException rootContextException) {
                try { // Last resort: maybe this library is afforded the privilege by the type's module.
                    acquiredAccess = MethodHandles.privateLookupIn(target, MethodHandles.lookup());
                } catch (IllegalAccessException | SecurityException libraryLookupException) {
                    // IMPROVEMENT In case of final failure, attempt to find an accessible getter method
                    throw new TracedAccessException("Couldn't get privileged lookup access into: " + target.getCanonicalName()
                            + (forSuperclass ? "\n Accessing superclass of: " + parent.lookupClass()
                                            : ".\n Parent class: " + parentException.getMessage())
                            + ",\n root context: " + rootContextException.getMessage()
                            + ",\n library context: " + libraryLookupException.getMessage());
                }
            }
        }
        this.privilegedLookups.put(target, acquiredAccess);
        return acquiredAccess;
    }


}
