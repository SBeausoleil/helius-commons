package systems.helius.commons;

import systems.helius.commons.exceptions.InvalidFormatException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class SmartProperties extends Properties {
    public SmartProperties() {
    }

    public SmartProperties(int initialCapacity) {
        super(initialCapacity);
    }

    public SmartProperties(Properties defaults) {
        super(defaults);
    }

    /**
     * Attempt to get a boolean from the properties.
     *<p>
     *     The following patterns are supported:
     *     <ul>
     *         <li>true | false</li>
     *         <li>yes | no</li>
     *         <li>on | off</li>
     *         <li>1 | 0</li>
     *     </ul>
     *</p>
     *
     * @param key the property key to read
     * @param defaultValue the value to return if no property is mapped to the key.
     * @return the boolean value.
     * @throws InvalidFormatException if the value mapped to the key is not of a supported format
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return parseBoolean(value);
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value)
                || "1".equalsIgnoreCase(value))
            return true;
        if ("false".equalsIgnoreCase(value)
                || "no".equalsIgnoreCase(value)
                || "off".equalsIgnoreCase(value)
                || "0".equalsIgnoreCase(value))
            return false;
        throw new InvalidFormatException(value, "true|false", "yes|no", "on|off", "0|1");
    }

    public Optional<Boolean> getBoolean(String key) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null ? parseBoolean(value) : null);
    }

    public SmartProperties ifBooleanPresent(String key, Consumer<Boolean> then) {
        getBoolean(key).ifPresent(then);
        return this;
    }

    public byte getByte(String key, byte defaultValue) {
        String value = getProperty(key);
        return value != null ? Byte.parseByte(value) : defaultValue;
    }

    public Optional<Byte> getByte(String key) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null ? Byte.parseByte(value) : null);
    }

    public SmartProperties ifBytePresent(String key, Consumer<Byte> then) {
        getByte(key).ifPresent(then);
        return this;
    }

    public char getChar(String key, char defaultValue) {
        String value = getProperty(key);
        return value != null && !value.isEmpty() ? value.charAt(0) : defaultValue;
    }

    public Optional<Character> getChar(String key) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null && !value.isEmpty() ? value.charAt(0) : null);
    }

    public SmartProperties ifCharPresent(String key, Consumer<Character> then) {
        getChar(key).ifPresent(then);
        return this;
    }

    public short getShort(String key, short defaultValue) {
        String value = getProperty(key);
        return value != null ? Short.parseShort(value) : defaultValue;
    }

    public Optional<Short> getShort(String key) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null ? Short.parseShort(value) : null);
    }

    public SmartProperties ifShortPresent(String key, Consumer<Short> then) {
        getShort(key).ifPresent(then);
        return this;
    }

    public int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public OptionalInt getInt(String key) {
        String value = getProperty(key);
        return value != null ? OptionalInt.of(Integer.parseInt(value)) : OptionalInt.empty();
    }

    public SmartProperties ifIntPresent(String key, IntConsumer then) {
        getInt(key).ifPresent(then);
        return this;
    }

    public long getLong(String key, long defaultValue) {
        String value = getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    public OptionalLong getLong(String key) {
        String value = getProperty(key);
        return value != null ? OptionalLong.of(Long.parseLong(value)) : OptionalLong.empty();
    }

    public SmartProperties ifLongPresent(String key, LongConsumer then) {
        getLong(key).ifPresent(then);
        return this;
    }

    public float getFloat(String key, float defaultValue) {
        String value = getProperty(key);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    public Optional<Float> getFloat(String key) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null ? Float.parseFloat(value) : null);
    }

    public SmartProperties ifFloatPresent(String key, Consumer<Float> then) {
        getFloat(key).ifPresent(then);
        return this;
    }

    public double getDouble(String key, double defaultValue) {
        String value = getProperty(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }

    public OptionalDouble getDouble(String key) {
        String value = getProperty(key);
        return value != null ? OptionalDouble.of(Double.parseDouble(value)) : OptionalDouble.empty();
    }

    public SmartProperties ifDoublePresent(String key, DoubleConsumer then) {
        getDouble(key).ifPresent(then);
        return this;
    }

    public <T extends Enum<T>> Optional<T> getEnum(String key, Class<T> enumClass) {
        String value = getProperty(key);
        return Optional.ofNullable(value != null ? Enum.valueOf(enumClass, value) : null);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        return getEnum(key, enumClass).orElse(defaultValue);
    }

    public <T extends Enum<T>> SmartProperties ifEnumPresent(String key, Class<T> enumClass, Consumer<T> then) {
        getEnum(key, enumClass).ifPresent(then);
        return this;
    }
}
