package systems.helius.commons.exceptions;

public class RuntimeIntrospectionException extends RuntimeException {
    public RuntimeIntrospectionException() {
    }

    public RuntimeIntrospectionException(String message) {
        super(message);
    }

    public RuntimeIntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIntrospectionException(Throwable cause) {
        super(cause);
    }
}
