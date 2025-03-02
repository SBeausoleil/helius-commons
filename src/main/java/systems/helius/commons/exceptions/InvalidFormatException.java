package systems.helius.commons.exceptions;

import jakarta.annotation.Nullable;

/**
 * Indicate that a value that was required to be of a specific format was found not to match
 * any supported formats.
 */
public class InvalidFormatException extends IllegalArgumentException {
    private @Nullable String value;
    private String[] supported;

    /**
     * Construct a new InvalidFormatException
     * @param value the bad value
     * @param supported the supported formats
     */
    public InvalidFormatException(@Nullable String value, String... supported) {
        super(buildMessage(value, supported));
        this.value = value;
        this.supported = supported;
    }

    public static String buildMessage(@Nullable String value, String... supported) {
        var sb = new StringBuilder();
        if (value != null) {
            sb.append("The value \"");
            sb.append(value);
            sb.append("\" is not correctly formatted. ");
        } else {
            sb.append("The value is null and that is not supported. ");
        }
        addFormatsInfo(supported, sb);
        return sb.toString();
    }

    private static void addFormatsInfo(String[] supported, StringBuilder sb) {
        if (supported.length > 0) {
            sb.append("Supported formats are: [");
            sb.append(String.join(" or ", supported));
            sb.append("].");
        }
    }

}