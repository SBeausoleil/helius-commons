package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.exceptions.IntrospectionException;

import systems.helius.commons.reflection.accessors.AccessorsChain;
import systems.helius.commons.reflection.accessors.Content;

import java.lang.reflect.Field;
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
     * @throws IntrospectionException if any fatal access issues are encountered during the introspection.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/IdentityHashMap.html">Java 17 API: IdentitHashMap</a>
     */
    public <T> Set<T> seek(Class<T> targetType, Object root, Lookup context) throws IntrospectionException {
        Set<T> found = Collections.newSetFromMap(new IdentityHashMap<>());
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        try {
            depthFirstSearch(root, null, 0, new IntrospectionContext<>(targetType, context, found, visited, new AccessorsChain(classInspector, new LookupManager())),
                    defaults);
        } catch (TracedAccessException e) {
            e.setRoot(root);
            throw new IntrospectionException(e);
        }
        return found;
    }

    protected <T> void depthFirstSearch(Object current,
                                        @Nullable Field holdingField,
                                        int depth,
                                        IntrospectionContext<T> context,
                                        IntrospectionSettings settings) throws TracedAccessException {
        // Checks
        if (current == null || depth >= settings.getMaxDepth() || context.visited().contains(current))
            return;
        context.visited().add(current);

        if (ClassInspector.evaluateTypingMatch(context.targetType(), current, (holdingField != null ? holdingField.getType() : null))) {
            //noinspection unchecked covered by the static isAssignableFrom
            context.found().add((T) current);
            if (!settings.isEnterTargetType())
                return;
        }

        if (ClassInspector.isPrimitiveWrapper(current.getClass())) // Also catches primitives due to the autoboxing
            return;
        // End of checks

        descendInto(current, holdingField, depth, context, settings);
    }

    private <T> void descendInto(Object current, Field holdingField, int depth, IntrospectionContext<T> context, IntrospectionSettings settings) throws TracedAccessException {
        Collection<Content> content = null;
        try {
            content = context.contentAccessor().extract(current, holdingField, context, settings);
        } catch (Exception e) {
            if (!settings.useSafeAccessCheck()) {
                var traced = new TracedAccessException(e);
                traced.addStep(holdingField);
                throw traced;
            }
        }
        if (content == null || content.isEmpty()) {
            return;
        }

        final int currentDepth = depth + 1;
        for (Content c : content) {
            if (c == null) continue;
            depthFirstSearch(c.value(), c.holdingField(), currentDepth, context, settings);
        }
    }
}
