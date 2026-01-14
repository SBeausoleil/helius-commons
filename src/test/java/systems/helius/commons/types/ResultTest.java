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
    void GivenValidInput_WhenProtectingFunctionAndGetValue_ThenReturnsOkResult() {
        Result<Integer, SomeException> result = Result.protect(ResultTest::failingIntFunction).apply(5);
        assertTrue(result.isOk());
        assertTrue(result.value().isPresent());
        assertEquals(10, result.value().get());

        assertFalse(result.isErr());
        assertFalse(result.error().isPresent());
    }

    @Test
    void GivenInvalidInput_WhenProtectingFunctionAndGetError_ThenReturnsErrorResult() {
        Result<Integer, SomeException> result = Result.protect(ResultTest::failingIntFunction).apply(-3);
        assertTrue(result.isErr());
        assertTrue(result.error().isPresent());
        assertEquals(SomeException.class, result.error().get().getClass());

        assertFalse(result.isOk());
        assertFalse(result.value().isPresent());
    }
}