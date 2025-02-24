package systems.helius.commons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import systems.helius.commons.exceptions.InvalidFormatException;
import systems.helius.commons.tests.ConsumerCallCheck;


import static org.junit.jupiter.api.Assertions.*;

class SmartPropertiesTest {

    private SmartProperties smartProperties;
    private ConsumerCallCheck<?> consumerCallCheck = new ConsumerCallCheck<>();

    @BeforeEach
    void setUp() {
        smartProperties = new SmartProperties();
    }

    @AfterEach
    void afterEach() {
        consumerCallCheck.reset();
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "yes", "on", "1"})
    void getBooleanTrue(String trueValue) {
        smartProperties.setProperty("key", trueValue);
        assertTrue(smartProperties.getBoolean("key", false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "no", "off", "0"})
    void getBooleanFalse(String falseValue) {
        smartProperties.setProperty("key", falseValue);
        assertFalse(smartProperties.getBoolean("key", true));
    }

    @Test
    void getBooleanDefault_true() {
        assertTrue(smartProperties.getBoolean("nonexistentKey", true));
    }

    @Test
    void getBooleanDefault_false() {
        assertFalse(smartProperties.getBoolean("nonexistentKey", false));
    }

    @Test
    void getBooleanInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(InvalidFormatException.class, () -> smartProperties.getBoolean("key", false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void getOptionalBoolean_present(String value) {
        smartProperties.setProperty("key", value);
        assertTrue(smartProperties.getBoolean("key").isPresent());
        assertEquals(Boolean.parseBoolean(value), smartProperties.getBoolean("key").get());
    }

    @Test
    void getOptionalBoolean_empty() {
        assertFalse(smartProperties.getBoolean("nonexistentKey").isPresent());
    }

    @Test
    void ifBooleanPresent_thenCall() {
        smartProperties.setProperty("key", "true");
        smartProperties.ifBooleanPresent("key", consumerCallCheck.bend());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifBooleanPresent_notPresent() {
        smartProperties.ifBooleanPresent("nonexistentKey", consumerCallCheck.bend());
        assertFalse(consumerCallCheck.wasCalled());
    }
}