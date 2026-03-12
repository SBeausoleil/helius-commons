package systems.helius.commons.time;

import jakarta.annotation.Nullable;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

public final class TimeUtils {
    /**
     * The checked temporal units in descending order of precision.
     */
    private static final ChronoField[] CHECKED_FIELDS = {
            ChronoField.YEAR, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH,
            ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE,
            ChronoField.MILLI_OF_SECOND, ChronoField.MICRO_OF_SECOND, ChronoField.NANO_OF_SECOND
    };

    private TimeUtils() {
    }

    /**
     * Compares two {@link Temporal} up to the smallest precision level they have in common.
     * <p>
     * Useful for comparing data from a runtime against it's persisted equivalent where the persistence layer
     * may have stripped some precision.
     * </p>
     * <p>
     * The order of verification is as following:
     * <ol>
     *     <li>Year</li>
     *     <li>Month of year</li>
     *     <li>Day of month</li>
     *     <li>Hour of day</li>
     *     <li>Minute of hour</li>
     *     <li>Second of minute</li>
     *     <li>Milli of second</li>
     *     <li>Micro of second</li>
     *     <li>Nano of second</li>
     * </ol>
     * The comparison is done starting from the first level of precision supported by any of the arguments.
     * If the lowest precision level is not supported by both arguments, they are not considered equivalent.
     * e.g.:
     * </p>
     * <p>
     * <code>
     * 2026-03-12 02:02:48.364016  ==  2026-03-12 02:02:48.364016012  (Everything up to the millisecond (inclusive) is equal)
     * </code>
     * </p>
     * <p>
     * <code>
     * 2026-03-12 02:02:48.364016  !=  2026-03-12 02:01:48.364016     (The minutes are different, so they are not roughly equal)
     * </code>
     * </p>
     *
     * @param a the first temporal
     * @param b the second temporal
     * @return true if both are equals up to their smallest shared temporal unit. True if both temporals are null. False if only one is null.
     */
    public static boolean isRoughlyEqual(@Nullable TemporalAccessor a, @Nullable TemporalAccessor b) {
        return TimeUtils.isRoughlyEqual(a, b, null);
    }

    /**
     * Compares two {@link Temporal} up to the smallest precision level they have in common.
     * <p>
     * Useful for comparing data from a runtime against it's persisted equivalent where the persistence layer
     * may have stripped some precision.
     * </p>
     * <p>
     * The order of verification is as following:
     * <ol>
     *     <li>Year</li>
     *     <li>Month of year</li>
     *     <li>Day of month</li>
     *     <li>Hour of day</li>
     *     <li>Minute of hour</li>
     *     <li>Second of minute</li>
     *     <li>Milli of second</li>
     *     <li>Micro of second</li>
     *     <li>Nano of second</li>
     * </ol>
     * The comparison is done starting from the first level of precision supported by any of the arguments.
     * If the lowest precision level is not supported by both arguments, they are not considered equivalent.
     * e.g.:
     * </p>
     * <p>
     * <code>
     * 2026-03-12 02:02:48.364016  ==  2026-03-12 02:02:48.364016012  (Everything up to the millisecond (inclusive) is equal)
     * </code>
     * </p>
     * <p>
     * <code>
     * 2026-03-12 02:02:48.364016  !=  2026-03-12 02:01:48.364016     (The minutes are different, so they are not roughly equal)
     * </code>
     * </p>
     *
     * @param a                 the first temporal
     * @param b                 the second temporal
     * @param ignoreSmallerThan (optional) all temporal units smaller than this unit (exclusive) are ignored for the comparison, e.g. if this is ChronoUnit.SECONDS, all units smaller than seconds are ignored
     * @return true if both are equals up to their smallest shared temporal unit. True if both temporals are null. False if only one is null.
     */
    public static boolean isRoughlyEqual(@Nullable TemporalAccessor a, @Nullable TemporalAccessor b, @Nullable ChronoField ignoreSmallerThan) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        boolean equalUpToNow = false;
        for (ChronoField chronoField : CHECKED_FIELDS) {
            if (ignoreSmallerThan != null && chronoField.compareTo(ignoreSmallerThan) < 0) {
                break;
            }
            if (a.isSupported(chronoField) != b.isSupported(chronoField)) {
                return equalUpToNow;
            }
            if (!a.isSupported(chronoField)) {
                continue;
            }
            int aFieldValue = a.get(chronoField);
            int bFieldValue = b.get(chronoField);
            if (aFieldValue != bFieldValue) {
                if (aFieldValue == 0 && isPrecisionTruncatedAt(a, chronoField)) {
                    return equalUpToNow;
                }
                if (bFieldValue == 0 && isPrecisionTruncatedAt(b, chronoField)) {
                    return equalUpToNow;
                }
                return false;
            }
            equalUpToNow = true;
        }
        return true;
    }

    /**
     * Checks if the temporal has no data (supported or otherwise zeroed) for the given precision level and smaller.
     *
     * @param temporal  the temporal object
     * @param precision the precision to check for truncation
     * @return true if every precision levels starting at precision and smaller are not supported or zeroed.
     */
    public static boolean isPrecisionTruncatedAt(TemporalAccessor temporal, ChronoField precision) {
        for (ChronoField level : CHECKED_FIELDS) {
            if (level.compareTo(precision) > 0) {
                continue;
            }
            if (temporal.isSupported(level)) {
                int value = switch (level) {
                    case MICRO_OF_SECOND, NANO_OF_SECOND -> temporal.get(level) % 1000; // Modulo to avoid getting the upper subsecond units
                    default -> temporal.get(level);
                };
                if (value != 0)
                    return false;
            }
        }
        return true;
    }
}
