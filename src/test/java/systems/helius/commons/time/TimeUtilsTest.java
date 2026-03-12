package systems.helius.commons.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    @Test
    void GivenEqualLocalDateTimes_WhenNoIgnoreField_ThenReturnsTrue() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertTrue(result);
    }

    @Test
    void GivenDifferentMinute_WhenNoIgnoreField_ThenReturnsFalse() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 1, 48, 364_016_012);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertFalse(result);
    }

    @Test
    void GivenDifferenceOnlyInNano_WhenIgnoringSmallerThanMicro_ThenReturnsTrue() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_000);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);

        boolean result = TimeUtils.isRoughlyEqual(a, b, ChronoField.MICRO_OF_SECOND);

        assertTrue(result);
    }

    @Test
    void GivenDifferenceInSecond_WhenIgnoringSmallerThanMinute_ThenReturnsTrue() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 0);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 2, 49, 0);

        boolean result = TimeUtils.isRoughlyEqual(a, b, ChronoField.MINUTE_OF_HOUR);

        assertTrue(result);
    }

    @Test
    void GivenBothTemporalsWithoutDateFields_WhenCompared_ThenSkipsUnsupportedAndCanStillReturnTrue() {
        LocalTime a = LocalTime.of(10, 15, 30, 123_000_000);
        LocalTime b = LocalTime.of(10, 15, 30, 123_000_000);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertTrue(result);
    }

    @Test
    void GivenBothTemporalsWithoutTimeFields_WhenCompared_ThenSkipsUnsupportedAndCanStillReturnTrue() {
        LocalDate a = LocalDate.of(2026, 3, 12);
        LocalDate b = LocalDate.of(2026, 3, 12);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertTrue(result);
    }

    @Test
    void GivenOneTemporalSupportsASmallerFieldAndTheOtherDoesNot_WhenCompared_ThenReturnsTrue() {
        LocalDate a = LocalDate.of(2026, 3, 12);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 0);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertTrue(result);
    }

    @Test
    void GivenOneTemporalSupportAHBiggerFieldAndTheOtherDoesNot_WhenCompared_ThenReturnsFalse() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 0);
        LocalTime b = LocalTime.of(2, 2, 48, 0);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertFalse(result);
    }

    @Test
    void GivenNoCommonInitialCheckedFieldSupport_WhenCompared_ThenReturnsFalse() {
        LocalDate a = LocalDate.of(2026, 3, 12);
        LocalTime b = LocalTime.of(2, 2, 48);

        boolean result = TimeUtils.isRoughlyEqual(a, b);

        assertFalse(result);
    }

    @Test
    void GivenIgnoreFieldThatBreaksBeforeAnyCheckedField_WhenCompared_ThenReturnsTrue() {
        LocalDate a = LocalDate.of(2026, 3, 12);
        LocalDate b = LocalDate.of(1999, 1, 1);

        boolean result = TimeUtils.isRoughlyEqual(a, b, ChronoField.ERA);

        assertTrue(result);
    }


    @Test
    void GivenSimilarTimestampsButTruncated_WhenCompared_ThenReturnsTrue() {
        LocalDateTime a = LocalDateTime.of(2026, 3, 12, 2, 2, 48).plus(15, ChronoUnit.MILLIS);
        LocalDateTime b = LocalDateTime.of(2026, 3, 12, 2, 2, 48);

        assertTrue(TimeUtils.isRoughlyEqual(a, b));
    }

    @Test
    void GivenMidnightLocalTime_WhenPrecisionIsHourOfDay_ThenReturnsTrue() {
        LocalTime temporal = LocalTime.MIDNIGHT;

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.HOUR_OF_DAY);

        assertTrue(result);
    }

    @Test
    void GivenLocalTimeWithHourSet_WhenPrecisionIsHourOfDay_ThenReturnsFalse() {
        LocalTime temporal = LocalTime.of(1, 0, 0, 0);

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.HOUR_OF_DAY);

        assertFalse(result);
    }

    @Test
    void GivenLocalDate_WhenPrecisionIsYear_ThenReturnsFalse() {
        LocalDate temporal = LocalDate.of(2026, 3, 12);

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.YEAR);

        assertFalse(result);
    }

    @Test
    void GivenAnyTemporal_WhenPrecisionIsEra_ThenReturnsFalse() {
        LocalDate temporal = LocalDate.of(2026, 3, 12);

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.ERA);

        assertFalse(result);
    }

    @Test
    void GivenLocalTimeWithMilliPrecision_WhenPrecisionIsMicroOfSecond_ThenReturnsTrue() {
        LocalTime temporal = LocalTime.of(15, 15, 15, 123_000_000);

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.MICRO_OF_SECOND);

        assertTrue(result);
    }

    @Test
    void GivenLocalTimeWithNanoSet_WhenPrecisionIsNanoOfSecond_ThenReturnsFalse() {
        LocalTime temporal = LocalTime.of(0, 0, 0, 1);

        boolean result = TimeUtils.isPrecisionTruncatedAt(temporal, ChronoField.NANO_OF_SECOND);

        assertFalse(result);
    }

    @Test
    void GivenBothInputsNull_WhenCompared_ThenTrue() {
        assertTrue(TimeUtils.isRoughlyEqual(null, null));
    }

    @Test
    void GivenOneNullInputAndOtherValid_WhenCompared_ThenFalse() {
        assertFalse(TimeUtils.isRoughlyEqual(null, LocalDate.now()));
        assertFalse(TimeUtils.isRoughlyEqual(LocalDate.now(), null));
    }
}
