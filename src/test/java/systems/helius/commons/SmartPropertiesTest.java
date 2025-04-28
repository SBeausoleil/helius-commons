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
        assertTrue(smartProperties.getBoolean("nonexistentKey").isEmpty());
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

    @ParameterizedTest
    @ValueSource(strings = {"0", "127", "-128"})
    void getByteValid(String byteValue) {
        smartProperties.setProperty("key", byteValue);
        assertEquals(Byte.parseByte(byteValue), smartProperties.getByte("key", (byte) 0));
    }

    @Test
    void getByteDefault() {
        assertEquals((byte) 10, smartProperties.getByte("nonexistentKey", (byte) 10));
    }

    @Test
    void getByteInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getByte("key", (byte) 0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "127", "-128"})
    void getOptionalByte_present(String value) {
        smartProperties.setProperty("key", value);
        assertTrue(smartProperties.getByte("key").isPresent());
        assertEquals(Byte.parseByte(value), smartProperties.getByte("key").get());
    }

    @Test
    void getOptionalByte_empty() {
        assertTrue(smartProperties.getByte("nonexistentKey").isEmpty());
    }

    @Test
    void ifBytePresent_thenCall() {
        smartProperties.setProperty("key", "127");
        smartProperties.ifBytePresent("key", consumerCallCheck.bend());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifBytePresent_notPresent() {
        smartProperties.ifBytePresent("nonexistentKey", consumerCallCheck.bend());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "c"})
    void getCharValid(String charValue) {
        smartProperties.setProperty("key", charValue);
        assertEquals(charValue.charAt(0), smartProperties.getChar("key", 'z'));
    }

    @Test
    void getCharDefault() {
        assertEquals('z', smartProperties.getChar("nonexistentKey", 'z'));
    }

    @Test
    void getOptionalChar_present() {
        smartProperties.setProperty("key", "a");
        assertTrue(smartProperties.getChar("key").isPresent());
        assertEquals('a', smartProperties.getChar("key").get());
    }

    @Test
    void getOptionalChar_empty() {
        assertTrue(smartProperties.getChar("nonexistentKey").isEmpty());
    }

    @Test
    void ifCharPresent_thenCall() {
        smartProperties.setProperty("key", "a");
        smartProperties.ifCharPresent("key", consumerCallCheck.bend());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifCharPresent_notPresent() {
        smartProperties.ifCharPresent("nonexistentKey", consumerCallCheck.bend());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(shorts = {0, 32767, -32768})
    void getShortValid(short shortValue) {
        smartProperties.setProperty("key", Short.toString(shortValue));
        assertEquals(shortValue, smartProperties.getShort("key", (short) 0));
    }

    @Test
    void getShortDefault() {
        assertEquals((short) 10, smartProperties.getShort("nonexistentKey", (short) 10));
    }

    @Test
    void getShortInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getShort("key", (short) 0));
    }

    @ParameterizedTest
    @ValueSource(shorts = {0, 32767, -32768})
    void getOptionalShort_present(short value) {
        smartProperties.setProperty("key", Short.toString(value));
        assertTrue(smartProperties.getShort("key").isPresent());
        assertEquals(value, smartProperties.getShort("key").get());
    }

    @Test
    void getOptionalShort_empty() {
        assertTrue(smartProperties.getShort("nonexistentKey").isEmpty());
    }

    @Test
    void ifShortPresent_thenCall() {
        smartProperties.setProperty("key", "32767");
        smartProperties.ifShortPresent("key", consumerCallCheck.bend());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifShortPresent_notPresent() {
        smartProperties.ifShortPresent("nonexistentKey", consumerCallCheck.bend());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2147483647, -2147483648})
    void getIntValid(int intValue) {
        smartProperties.setProperty("key", Integer.toString(intValue));
        assertEquals(intValue, smartProperties.getInt("key", 0));
    }

    @Test
    void getIntDefault() {
        assertEquals(10, smartProperties.getInt("nonexistentKey", 10));
    }

    @Test
    void getIntInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getInt("key", 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2147483647, -2147483648})
    void getOptionalInt_present(int value) {
        smartProperties.setProperty("key", Integer.toString(value));
        assertTrue(smartProperties.getInt("key").isPresent());
        assertEquals(value, smartProperties.getInt("key").getAsInt());
    }

    @Test
    void getOptionalInt_empty() {
        assertTrue(smartProperties.getInt("nonexistentKey").isEmpty());
    }

    @Test
    void ifIntPresent_thenCall() {
        smartProperties.setProperty("key", "2147483647");
        smartProperties.ifIntPresent("key", consumerCallCheck.bendInt());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifIntPresent_notPresent() {
        smartProperties.ifIntPresent("nonexistentKey", consumerCallCheck.bendInt());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 9223372036854775807L, -9223372036854775808L})
    void getLongValid(long longValue) {
        smartProperties.setProperty("key", Long.toString(longValue));
        assertEquals(longValue, smartProperties.getLong("key", 0L));
    }

    @Test
    void getLongDefault() {
        assertEquals(10L, smartProperties.getLong("nonexistentKey", 10L));
    }

    @Test
    void getLongInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getLong("key", 0L));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 9223372036854775807L, -9223372036854775808L})
    void getOptionalLong_present(long value) {
        smartProperties.setProperty("key", Long.toString(value));
        assertTrue(smartProperties.getLong("key").isPresent());
        assertEquals(value, smartProperties.getLong("key").getAsLong());
    }

    @Test
    void getOptionalLong_empty() {
        assertTrue(smartProperties.getLong("nonexistentKey").isEmpty());
    }

    @Test
    void ifLongPresent_thenCall() {
        smartProperties.setProperty("key", "9223372036854775807");
        smartProperties.ifLongPresent("key", consumerCallCheck.bendLong());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifLongPresent_notPresent() {
        smartProperties.ifLongPresent("nonexistentKey", consumerCallCheck.bendLong());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "3.14", "-2.71"})
    void getFloatValid(String floatValue) {
        smartProperties.setProperty("key", floatValue);
        assertEquals(Float.parseFloat(floatValue), smartProperties.getFloat("key", 0.0f));
    }

    @Test
    void getFloatDefault() {
        assertEquals(10.5f, smartProperties.getFloat("nonexistentKey", 10.5f));
    }

    @Test
    void getFloatInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getFloat("key", 0.0f));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "3.14", "-2.71"})
    void getOptionalFloat_present(String value) {
        smartProperties.setProperty("key", value);
        assertTrue(smartProperties.getFloat("key").isPresent());
        assertEquals(Float.parseFloat(value), smartProperties.getFloat("key").get());
    }

    @Test
    void getOptionalFloat_empty() {
        assertTrue(smartProperties.getFloat("nonexistentKey").isEmpty());
    }

    @Test
    void ifFloatPresent_thenCall() {
        smartProperties.setProperty("key", "3.14");
        smartProperties.ifFloatPresent("key", consumerCallCheck.bend());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifFloatPresent_notPresent() {
        smartProperties.ifFloatPresent("nonexistentKey", consumerCallCheck.bend());
        assertFalse(consumerCallCheck.wasCalled());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "3.14", "-2.71"})
    void getDoubleValid(String doubleValue) {
        smartProperties.setProperty("key", doubleValue);
        assertEquals(Double.parseDouble(doubleValue), smartProperties.getDouble("key", 0.0));
    }

    @Test
    void getDoubleDefault() {
        assertEquals(10.5, smartProperties.getDouble("nonexistentKey", 10.5));
    }

    @Test
    void getDoubleInvalidFormat() {
        smartProperties.setProperty("key", "invalid");
        assertThrows(NumberFormatException.class, () -> smartProperties.getDouble("key", 0.0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "3.14", "-2.71"})
    void getOptionalDouble_present(String value) {
        smartProperties.setProperty("key", value);
        assertTrue(smartProperties.getDouble("key").isPresent());
        assertEquals(Double.parseDouble(value), smartProperties.getDouble("key").getAsDouble());
    }

    @Test
    void getOptionalDouble_empty() {
        assertTrue(smartProperties.getDouble("nonexistentKey").isEmpty());
    }

    @Test
    void ifDoublePresent_thenCall() {
        smartProperties.setProperty("key", "3.14");
        smartProperties.ifDoublePresent("key", consumerCallCheck.bendDouble());
        assertTrue(consumerCallCheck.wasCalledOnce());
    }

    @Test
    void ifDoublePresent_notPresent() {
        smartProperties.ifDoublePresent("nonexistentKey", consumerCallCheck.bendDouble());
        assertFalse(consumerCallCheck.wasCalled());
    }
}