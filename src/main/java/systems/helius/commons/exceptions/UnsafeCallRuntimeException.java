package systems.helius.commons.exceptions;

public class UnsafeCallRuntimeException extends RuntimeException {
    public UnsafeCallRuntimeException() {
    }

    public UnsafeCallRuntimeException(String message) {
        super(message);
    }

    public UnsafeCallRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeCallRuntimeException(Throwable cause) {
        super(cause);
    }
}
