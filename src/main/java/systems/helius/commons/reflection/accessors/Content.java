package systems.helius.commons.reflection.accessors;

import jakarta.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * Represents a part of an object, including its value and the field that holds it.
 *
 * @param value        the value contained by the object
 * @param holdingField the field that holds this value, or null if not applicable
 */
public record Content(Object value, @Nullable Field holdingField) {
}
