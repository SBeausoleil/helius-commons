package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.annotations.Internal;

import java.io.Serial;
import java.lang.reflect.Field;
import java.util.Stack;

@Internal
public class TracedAccessException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    @Nullable
    private Object root;
    private Stack<Field> trace = new Stack<>();
    private boolean assertionError;

    public TracedAccessException(String message) {
        super(message);
    }

    public TracedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addStep(@Nullable Field step) {
        if (step != null)
            trace.push(step);
    }

    private String buildMessage() {
        StringBuilder sb = new StringBuilder(getMessage());
        sb.append("Path leading to issue: ");
        if (root != null) {
            sb.append("[root]: ");
            sb.append(root.getClass().getCanonicalName());
            sb.append(": ");
            sb.append(root);
        }
        for (Field step : trace) {
            sb.append(step.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isAssertionError() {
        return assertionError;
    }

    public boolean isException() {
        return !assertionError;
    }

    public void setAssertionError(boolean assertionError) {
        this.assertionError = assertionError;
    }

    @Nullable
    public Object getRoot() {
        return root;
    }

    public void setRoot(@Nullable Object root) {
        this.root = root;
    }

    /**
     * Transform this traced exception back into a regular IllegalAccessException as if it happened at the location of this exception.
     * @return an IllegalAccessException with a message detailing the path to the illegal access.
     */
    public IllegalAccessException toIllegalAccessException() {
        var exception = new IllegalAccessException(buildMessage());
        exception.setStackTrace(getStackTrace());
        return exception;
    }

    /**
     * Transform this traced exception back into a regular IllegalAccessError as if it happened at the location of this exception.
     * Unlike the exception variant, designates that the original access exception occurred somewhere it really should not be
     * possible to occur.
     * @return an IllegalAccessException with a message detailing the path to the illegal access.
     */
    public IllegalAccessError toIllegalAccessError() {
        var error = new IllegalAccessError(buildMessage());
        error.setStackTrace(getStackTrace());
        return error;
    }
}