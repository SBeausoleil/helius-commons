package systems.helius.commons.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidFormatExceptionTest {
    @Test
    void testBuildMessage() {
        String result = InvalidFormatException.buildMessage("test", "json", "xml");
        assertEquals("The value \"test\" is not correctly formatted. Supported formats are: [json or xml].", result);
    }

    @Test
    void testBuildMessageWithNullValue() {
        String result = InvalidFormatException.buildMessage(null, "json", "xml");
        assertEquals("The value is null and that is not supported. Supported formats are: [json or xml].", result);
    }
}