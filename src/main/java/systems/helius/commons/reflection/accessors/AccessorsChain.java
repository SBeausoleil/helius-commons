package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;
import systems.helius.commons.reflection.ClassInspector;
import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.IntrospectionSettings;
import systems.helius.commons.reflection.LookupManager;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class AccessorsChain implements ContentAccessor {
    private List<ContentAccessor> chain;

    public AccessorsChain(ClassInspector classInspector, LookupManager lookupManager) {
        this.chain = List.of(
                new IterativeAccessor(),
                new FieldHandlesAccessor(classInspector, lookupManager)
        );
    }

    @Override
    public boolean accepts(Object current, @Nullable Field holdingField, IntrospectionSettings settings) {
        return chain.stream().anyMatch(chainElement -> chainElement.accepts(current, holdingField, settings));
    }

    /**
     * Attempt to extract the content of the current object
     *
     * @param current      the current value to access the innards of.
     * @param holdingField the field that contained the current value.
     *                     Null when current is the root of the search.
     * @param context      the current introspection context
     * @param settings     settings of the current search
     * @return the content of the object
     * @throws ChainComponentException if a component of the chain throws an exception, it is thrown immediately if the exception does not allow for fallbacks.
     *                                 Otherwise, it is thrown only if none of the components managed to extract content and at least one threw an exception.
     */
    @Override
    public Collection<Content> extract(Object current, @Nullable Field holdingField, IntrospectionContext<?> context, IntrospectionSettings settings) throws ChainComponentException {
        ChainComponentException delayedException = null;
        Collection<Content> extracted = null;
        for (ContentAccessor chainElement : chain) {
            if (chainElement.accepts(current, holdingField, settings)) {
                try {
                    extracted = chainElement.extract(current, holdingField, context, settings);
                    break;
                } catch (ChainComponentException e) {
                    if (!e.isAllowFallback()) {
                        throw e;
                    }
                    delayedException = e;
                }
            }
        }
        if (extracted == null) { // If nothing is found
            if (delayedException != null) {
                // TODO consider merging exceptions if multiple chain elements threw exceptions
                // If we had a delayed exception, rethrow it
                throw delayedException;
            }
            // TODO check if any exception was thrown and if so, rethrow it
            return Collections.emptyList();
        }
        return extracted;
    }
}
