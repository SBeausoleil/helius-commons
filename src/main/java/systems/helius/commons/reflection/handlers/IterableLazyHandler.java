package systems.helius.commons.reflection.handlers;


import systems.helius.commons.reflection.HandlerResponse;
import systems.helius.commons.reflection.HandlerResponseBuilder;
import systems.helius.commons.reflection.IntrospectionContext;
import systems.helius.commons.reflection.ValueHandler;

import java.util.function.Function;

public class IterableLazyHandler implements ValueHandler {
    @Override
    public boolean accepts(Object instance, Class<?> sought) {
        return instance instanceof Iterable && !Iterable.class.isAssignableFrom(sought);
    }

    @Override
    public HandlerResponse handle(IntrospectionContext context) {
        return new HandlerResponseBuilder()
                .withGetter(Function.identity())
                .build();
    }
}
