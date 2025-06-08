package systems.helius.commons.reflection.accessors;

public abstract class BaseContentAccessor implements ContentAccessor {

    protected final int priority;

    protected BaseContentAccessor(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ContentAccessor o) {
        return Integer.compare(this.priority, o.priority);
    }
}
