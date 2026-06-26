package systems.helius.commons.types;

import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;
import systems.helius.commons.reflection.accessors.ChainComponentException;
import systems.helius.commons.reflection.accessors.Content;
import systems.helius.commons.reflection.accessors.ContentAccessor;

import jakarta.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link ContentAccessor} for {@link Department} objects that retrieves values by
 * invoking getter methods via {@link java.lang.reflect.Method#invoke(Object, Object...)}.
 *
 * <p>This accessor is the "getter-based" variant: it calls the public getter methods on
 * {@code Department} rather than reading fields directly.</p>
 */
public class DepartmentGetterAccessor implements ContentAccessor {

    private static final Method GET_NAME;
    private static final Method GET_STAFF;
    private static final Method GET_FOCUS_AREAS;
    private static final Method GET_COURSE_CATALOG;

    // Field references are used only for the Content wrappers (which require a Field).
    private static final Field NAME_FIELD;
    private static final Field STAFF_FIELD;
    private static final Field FOCUS_AREAS_FIELD;
    private static final Field COURSE_CATALOG_FIELD;

    static {
        try {
            GET_NAME = Department.class.getMethod("getName");
            GET_STAFF = Department.class.getMethod("getStaff");
            GET_FOCUS_AREAS = Department.class.getMethod("getFocusAreas");
            GET_COURSE_CATALOG = Department.class.getMethod("getCourseCatalog");

            NAME_FIELD = Department.class.getDeclaredField("name");
            STAFF_FIELD = Department.class.getDeclaredField("staff");
            FOCUS_AREAS_FIELD = Department.class.getDeclaredField("focusAreas");
            COURSE_CATALOG_FIELD = Department.class.getDeclaredField("courseCatalog");
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public boolean accepts(Class<?> current, @Nullable Field holdingField) {
        return Department.class.isAssignableFrom(current);
    }

    @Override
    public Collection<Content> extract(Object current,
                                       @Nullable Field holdingField,
                                       IntrospectionContext<?> context,
                                       IntrospectionSettings settings) throws ChainComponentException {
        List<Content> contents = new ArrayList<>(4);
        try {
            addIfNotNull(contents, GET_NAME.invoke(current), NAME_FIELD);
            addIfNotNull(contents, GET_STAFF.invoke(current), STAFF_FIELD);
            addIfNotNull(contents, GET_FOCUS_AREAS.invoke(current), FOCUS_AREAS_FIELD);
            addIfNotNull(contents, GET_COURSE_CATALOG.invoke(current), COURSE_CATALOG_FIELD);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ChainComponentException(e, false);
        }
        return contents;
    }

    private static void addIfNotNull(List<Content> contents, Object value, Field field) {
        if (value != null) {
            contents.add(new Content(value, field));
        }
    }
}
