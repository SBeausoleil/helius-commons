package systems.helius.commons.types;

import org.junit.jupiter.api.Test;

import java.util.IllegalFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    static class SomeException extends Exception {
        public SomeException(String message) {
            super(message);
        }
    }

    static int failingIntFunction(int x) throws SomeException {
        if (x < 0) {
            throw new SomeException("Negative value: " + x);
        }
        return x * 2;
    }

    @Test
    void GivenValidInput_WhenProtectingFunction_ThenReturnsOkResult() {
        Result<Integer, SomeException> result = Result.protect(ResultTest::failingIntFunction).apply(5);
        assertTrue(result.isOk());
        assertEquals(10, result.getValue());
    }

    @Test
    void GivenInvalidInput_WhenProtectingFunction_ThenReturnsErrorResult() {
        Result<Integer, SomeException> result = Result.protect(ResultTest::failingIntFunction).apply(-3);
        assertTrue(result.isErr());
        assertThrows(SomeException.class, result::getValue);
    }
}