package systems.helius.commons.reflection.accessors;

public class ChainComponentException extends Exception {
    /**
     * Indicates whether another accessor should be allowed to attempt to access the same value.
     */
    protected final boolean allowDelegation;

    public ChainComponentException(String message) {
        super(message);
        this.allowDelegation = true; // Default to allowing delegation
    }

    public ChainComponentException(String message, Throwable cause) {
        super(message, cause);
        this.allowDelegation = true; // Default to allowing delegation
    }

    public ChainComponentException(Throwable cause) {
        super(cause);
        this.allowDelegation = true; // Default to allowing delegation
    }

    public ChainComponentException(String message, boolean allowDelegation) {
        super(message);
        this.allowDelegation = allowDelegation;
    }

    public ChainComponentException(String message, Throwable cause, boolean allowDelegation) {
        super(message, cause);
        this.allowDelegation = allowDelegation;
    }

    public ChainComponentException(Throwable cause, boolean allowDelegation) {
        super(cause);
        this.allowDelegation = allowDelegation;
    }

    public boolean isAllowDelegation() {
        return allowDelegation;
    }
}
