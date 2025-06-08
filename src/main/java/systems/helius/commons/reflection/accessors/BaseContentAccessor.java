package systems.helius.commons.reflection.accessors;

public abstract class BaseContentAccessor implements ContentAccessor {

    protected final int priority;

    protected BaseContentAccessor(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
