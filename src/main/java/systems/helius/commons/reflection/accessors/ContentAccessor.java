package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;
import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;
import systems.helius.commons.reflection.TracedAccessException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Provides access to the content of an Object.
 */
public interface ContentAccessor {
    /**
     * Checks if this accessor accepts the current value.
     *
     * @param current      the current value to access the innards of.
     * @param holdingField the field that contained the current value.
     *                     Null when current is the root of the search.
     * @param settings
     * @return the values within the object.
     */
    boolean accepts(Object current, @Nullable Field holdingField, IntrospectionSettings settings);

    /**
     * Extract the values present within the current object.
     *
     * @param current      the current value to access the innards of.
     * @param holdingField the field that contained the current value.
     *                     Null when current is the root of the search.
     * @param context      the current introspection context
     * @param settings     settings of the current search
     * @param <T>          the type of values being sought.
     * @return a stream of the values within the current object.
     * This stream is not obligated to represent every single fields within the object,
     * it contains what matters to look into.
     * @throws ChainComponentException an extraction is authorized to fail.
     *                         The accessor must indicate whether the introspector
     *                         is allowed to try other accessors for the same value.
     */
    <T> Collection<Content> extract(Object current,
                                    @Nullable Field holdingField,
                                    IntrospectionContext<T> context,
                                    IntrospectionSettings settings) throws ChainComponentException;
}
