package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;
import systems.helius.commons.exceptions.LoookupAcquisitionException;
import systems.helius.commons.reflection.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FieldHandlesAccessor implements ContentAccessor {
    private final ClassInspector classInspector;
    private final LookupManager lookupManager;

    public FieldHandlesAccessor(ClassInspector classInspector, LookupManager lookupManager) {
        this.classInspector = classInspector;
        this.lookupManager = lookupManager;
    }

    @Override
    public boolean accepts(Object current, @Nullable Field holdingField, IntrospectionSettings settings) {
        return true;
    }

    @Override
    public <T> Stream<Content> extract(Object current, @Nullable Field holdingField, IntrospectionContext<T> context, IntrospectionSettings settings) throws ChainComponentException {
        Map<Class<?>, List<Field>> fields = classInspector.getAllFieldsHierarchical(current.getClass());
        if (fields.isEmpty()) return Stream.empty();

        var result = new ArrayList<Content>();

        MethodHandles.Lookup classLookup = getClassLookup(current, context);
        for (Map.Entry<Class<?>, List<Field>> entry : fields.entrySet()) {
            if (classLookup.lookupClass() != entry.getKey()) {
                // This grants access to the private fields within superclasses
                try {
                    classLookup = lookupManager.getPrivilegedLookup(entry.getKey(), classLookup, context.rootLookup(), true);
                } catch (LoookupAcquisitionException e) {
                    if (!settings.useSafeAccessCheck()) {
                        throw new ChainComponentException(e.getMessage(), e, true);
                    }
                    continue;
                }
            }

            for (Field field : entry.getValue()) {
                try {
                    if (Modifier.isStatic(field.getModifiers()))
                        return null;

                    Object value = classLookup.unreflectVarHandle(field).get(current);
                    if (value != null) {
                        result.add(new Content(value, field));
                    }
                } catch (IllegalAccessException e) {
                    var traced = new TracedAccessException("Couldn't read the value of the field: " + field
                            + ". This should be impossible. " +
                            "Please file an issue at https://github.com/SBeausoleil/helius-commons/issues" +
                            " describing how this happened.", e);
                    throw new ChainComponentException(traced, true);
                }
            }
        }
        return result.stream();
    }

    private <T> MethodHandles.Lookup getClassLookup(Object current, IntrospectionContext<T> context) throws ChainComponentException {
        MethodHandles.Lookup fakeParent = MethodHandles.lookup(); // TODO TEMPORARY ONLY FOR TESTS TO REPLACE THE PARENT LOOKUP
        try {
            return lookupManager.getPrivilegedLookup(current.getClass(), context.rootLookup(), fakeParent/*parent*/, false);
        } catch (LoookupAcquisitionException e) {
            throw new ChainComponentException(e, true);
        }
    }
}
