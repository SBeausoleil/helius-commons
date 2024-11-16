package systems.helius.commons.reflection;

import java.lang.reflect.Field;
import java.util.function.Function;

public final class HandlerResponseBuilder {
    private Function<Object, Object> getter;
    private Instruction instruction;
    private Field continueAt;

    public HandlerResponseBuilder() {
    }

    public HandlerResponseBuilder withGetter(Function<Object, Object> getter) {
        this.getter = getter;
        return this;
    }

    public HandlerResponseBuilder withInstruction(Instruction instruction) {
        this.instruction = instruction;
        return this;
    }

    public HandlerResponseBuilder withContinueAt(Field continueAt) {
        this.continueAt = continueAt;
        return this;
    }

    public HandlerResponse build() {
        HandlerResponse handlerResponse = new HandlerResponse();
        handlerResponse.setGetter(getter);
        handlerResponse.setInstruction(instruction);
        handlerResponse.setContinueAt(continueAt);
        return handlerResponse;
    }
}
