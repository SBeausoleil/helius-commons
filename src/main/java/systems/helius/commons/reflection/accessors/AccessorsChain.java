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

    @Override
    public <T> Collection<Content> extract(Object current, @Nullable Field holdingField, IntrospectionContext<T> context, IntrospectionSettings settings) throws ChainComponentException {
        ChainComponentException delayedException = null;
        for (ContentAccessor chainElement : chain) {
            if (chainElement.accepts(current, holdingField, settings)) {
                try {
                    return chainElement.extract(current, holdingField, context, settings);
                } catch (ChainComponentException e) {
                    // TODO check if a cause is a TracedException
                    if (!e.isAllowFallback()) {
                        throw e;
                    }
                    delayedException = e;
                }
            }
        }
        if (delayedException != null) {
            // TODO consider merging exceptions if multiple chain elements threw exceptions
            // If we had a delayed exception, rethrow it
            throw delayedException;
        }
        // TODO check if any exception was thrown and if so, rethrow it
        return Collections.emptyList();
    }
}
