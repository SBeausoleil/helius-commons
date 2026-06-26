package systems.helius.commons.types;

import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;
import systems.helius.commons.reflection.accessors.ChainComponentException;
import systems.helius.commons.reflection.accessors.Content;
import systems.helius.commons.reflection.accessors.ContentAccessor;

import jakarta.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link ContentAccessor} for {@link Course} objects that reads field values directly
 * via {@link java.lang.reflect.Field#get(Object)} (with {@code setAccessible(true)}).
 *
 * <p>This accessor is the "direct field reads" variant: it bypasses any getters and
 * accesses the backing fields of {@code Course} directly through the reflection API.</p>
 */
public class CourseDirectFieldAccessor implements ContentAccessor {

    private static final Field TITLE_FIELD;
    private static final Field DESCRIPTION_FIELD;
    private static final Field TAGS_FIELD;
    private static final Field PREREQUISITES_FIELD;
    private static final Field GRADING_CRITERIA_FIELD;

    static {
        try {
            TITLE_FIELD = Course.class.getDeclaredField("title");
            DESCRIPTION_FIELD = Course.class.getDeclaredField("description");
            TAGS_FIELD = Course.class.getDeclaredField("tags");
            PREREQUISITES_FIELD = Course.class.getDeclaredField("prerequisites");
            GRADING_CRITERIA_FIELD = Course.class.getDeclaredField("gradingCriteria");

            TITLE_FIELD.setAccessible(true);
            DESCRIPTION_FIELD.setAccessible(true);
            TAGS_FIELD.setAccessible(true);
            PREREQUISITES_FIELD.setAccessible(true);
            GRADING_CRITERIA_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public boolean accepts(Class<?> current, @Nullable Field holdingField) {
        return Course.class.isAssignableFrom(current);
    }

    @Override
    public Collection<Content> extract(Object current,
                                       @Nullable Field holdingField,
                                       IntrospectionContext<?> context,
                                       IntrospectionSettings settings) throws ChainComponentException {
        List<Content> contents = new ArrayList<>(5);
        try {
            contents.add(new Content(TITLE_FIELD.get(current), TITLE_FIELD));
            contents.add(new Content(DESCRIPTION_FIELD.get(current), DESCRIPTION_FIELD));
            contents.add(new Content(TAGS_FIELD.get(current), TAGS_FIELD));
            contents.add(new Content(PREREQUISITES_FIELD.get(current), PREREQUISITES_FIELD));
            contents.add(new Content(GRADING_CRITERIA_FIELD.get(current), GRADING_CRITERIA_FIELD));
        } catch (IllegalAccessException e) {
            throw new ChainComponentException(e, false);
        }
        return contents;
    }
}
