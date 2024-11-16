package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.function.Function;


public class HandlerResponse {
    @Nullable
    private Function<Object, Object> getter;
    @Nullable
    private Instruction instruction;
    @Nullable
    private Field continueAt;

    public HandlerResponse() {}

    public HandlerResponse(@Nullable Function<Object, Object> getter, @Nullable Instruction instruction, @Nullable Field continueAt) {
        this.getter = getter;
        this.instruction = instruction;
        this.continueAt = continueAt;
    }

    @Nullable
    public Function<Object, Object> getGetter() {
        return getter;
    }

    public void setGetter(@Nullable Function<Object, Object> getter) {
        this.getter = getter;
    }

    @Nullable
    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(@Nullable Instruction instruction) {
        this.instruction = instruction;
    }

    @Nullable
    public Field getContinueAt() {
        return continueAt;
    }

    public void setContinueAt(@Nullable Field continueAt) {
        this.continueAt = continueAt;
    }
}
