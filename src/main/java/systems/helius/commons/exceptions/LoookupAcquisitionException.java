package systems.helius.commons.exceptions;


public class LoookupAcquisitionException extends IllegalAccessException {
    private static final long serialVersionUID = 1L;

    public LoookupAcquisitionException(String message) {
        super(message);
    }

    public LoookupAcquisitionException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
