package systems.helius.commons;

import jakarta.annotation.Nullable;
import systems.helius.commons.exceptions.InvalidFormatException;

import java.util.Properties;
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

    public Boolean getBooleanOrNull(String key) {
        String value = getProperty(key);
        return value != null ? parseBoolean(value) : null;
    }

    public SmartProperties ifBooleanPresent(String key, Consumer<Boolean> consumer) {
        Boolean value = getBooleanOrNull(key);
        if (value != null) {
            consumer.accept(null);
        }
        return this;
    }

    public byte getByte(String key, byte defaultValue) {
        String value = getProperty(key);
        return value != null ? Byte.parseByte(value) : defaultValue;
    }

    public @Nullable Byte getByteOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Byte.parseByte(value) : null;
    }

    public SmartProperties ifBytePresent(String key, Consumer<Byte> consumer) {
        Byte value = getByteOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public char getChar(String key, char defaultValue) {
        String value = getProperty(key);
        return value != null && !value.isEmpty() ? value.charAt(0) : defaultValue;
    }

    public @Nullable Character getCharOrNull(String key) {
        String value = getProperty(key);
        return value != null && !value.isEmpty() ? value.charAt(0) : null;
    }

    public SmartProperties ifCharPresent(String key, Consumer<Character> consumer) {
        Character value = getCharOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public short getShort(String key, short defaultValue) {
        String value = getProperty(key);
        return value != null ? Short.parseShort(value) : defaultValue;
    }

    public @Nullable Short getShortOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Short.parseShort(value) : null;
    }

    public SmartProperties ifShortPresent(String key, Consumer<Short> consumer) {
        Short value = getShortOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public Integer getIntOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    public SmartProperties ifIntPresent(String key, IntConsumer consumer) {
        Integer value = getIntOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public long getLong(String key, long defaultValue) {
        String value = getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    public Long getLongOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Long.parseLong(value) : null;
    }

    public SmartProperties ifLongPresent(String key, LongConsumer consumer) {
        Long value = getLongOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public float getFloat(String key, float defaultValue) {
        String value = getProperty(key);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    public Float getFloatOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Float.parseFloat(value) : null;
    }

    public SmartProperties ifFloatPresent(String key, Consumer<Float> consumer) {
        Float value = getFloatOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public double getDouble(String key, double defaultValue) {
        String value = getProperty(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }

    public Double getDoubleOrNull(String key) {
        String value = getProperty(key);
        return value != null ? Double.parseDouble(value) : null;
    }

    public SmartProperties ifDoublePresent(String key, DoubleConsumer consumer) {
        Double value = getDoubleOrNull(key);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public <T extends Enum<T>> @Nullable T getEnum(String key, Class<T> enumClass) {
        String value = getProperty(key);
        return value != null ? Enum.valueOf(enumClass, value) : null;
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        T value = getEnum(key, enumClass);
        return value != null ? value : defaultValue;
    }

    public <T extends Enum<T>> SmartProperties ifEnumPresent(String key, Class<T> enumClass, Consumer<T> consumer) {
        T value = getEnum(key, enumClass);
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }
}
