package systems.helius.commons.reflection;

public enum Instruction {
    /**
     * Even if this handler did not provide a mean to access the field's content, other handlers of the current series should not be attempted.
     * In the case of a FieldHandler that also returned a value getter, will also prevent value handlers to look at the values.
     */
    STOP_OTHER_HANDLERS,
}
