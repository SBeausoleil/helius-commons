package systems.helius.commons.time;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {
    @Nested
    class RoughlyEqualTests {
        @Test
        void GivenEqualLocalDateTimes_WhenNoIgnoreField_ThenReturnsTrue() {
            var a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);
            var b = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);

            boolean result = TimeUtils.isRoughlyEqual(a, b);

            assertTrue(result);
        }

        @Test
        void GivenLocalDateTimeDifferentMinute_WhenNoIgnoreField_ThenReturnsFalse() {
            var a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 364_016_012);
            var b = LocalDateTime.of(2026, 3, 12, 2, 1, 48, 364_016_012);

            boolean result = TimeUtils.isRoughlyEqual(a, b);

            assertFalse(result);
        }

        @Test
        void GivenBothTemporalsWithoutDateFields_WhenCompared_ThenSkipsUnsupportedAndCanStillReturnTrue() {
            var a = LocalTime.of(10, 15, 30, 123_000_000);
            var b = LocalTime.of(10, 15, 30, 123_000_000);

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
            var a = LocalDate.of(2026, 3, 12);
            var b = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 0);

            boolean result = TimeUtils.isRoughlyEqual(a, b);

            assertTrue(result);
        }

        @Test
        void GivenOneTemporalSupportAHBiggerFieldAndTheOtherDoesNot_WhenCompared_ThenReturnsFalse() {
            var a = LocalDateTime.of(2026, 3, 12, 2, 2, 48, 0);
            var b = LocalTime.of(2, 2, 48, 0);

            boolean result = TimeUtils.isRoughlyEqual(a, b);

            assertFalse(result);
        }

        @Test
        void GivenNoCommonInitialCheckedFieldSupport_WhenCompared_ThenReturnsFalse() {
            var a = LocalDate.of(2026, 3, 12);
            var b = LocalTime.of(2, 2, 48);

            boolean result = TimeUtils.isRoughlyEqual(a, b);

            assertFalse(result);
        }

        @Test
        void GivenSimilarTimestampsButTruncated_WhenCompared_ThenReturnsTrue() {
            var a = LocalDateTime.of(2026, 3, 12, 2, 2, 48).plus(15, ChronoUnit.MILLIS);
            var b = LocalDateTime.of(2026, 3, 12, 2, 2, 48);

            assertTrue(TimeUtils.isRoughlyEqual(a, b));
        }

        @Test
        void GivenBothInputsNull_WhenIsRoughlyEqual_ThenTrue() {
            assertTrue(TimeUtils.isRoughlyEqual(null, null));
        }

        @Test
        void GivenOneNullInputAndOtherValid_WhenIsRoughlyEqual_ThenFalse() {
            assertFalse(TimeUtils.isRoughlyEqual(null, LocalDate.now()));
            assertFalse(TimeUtils.isRoughlyEqual(LocalDate.now(), null));
        }

        @Test
        void GivenTwoDifferentTimes_WhenIsRoughlyEqual_ThenFalse() {
            var a = LocalTime.of(10, 15, 30);
            var b = LocalTime.of(15, 0, 0);

            assertFalse(TimeUtils.isRoughlyEqual(a, b));
        }

        @Test
        void GivenDatesInDifferentEras_WhenIsRoughlyEqual_ThenFalse() {
            var a = LocalDate.of(2026, 3, 12).with(IsoEra.CE);
            var b = a.with(IsoEra.BCE);

            assertFalse(TimeUtils.isRoughlyEqual(a, b));
        }

        @Test
        void GivenEqualWithTruncation_WhenIsRoughlyEqualWithoutUpTo_ThenTrue() {
            var a = ZonedDateTime.of(2026, 3, 14, 18, 12, 9, 0, ZoneId.systemDefault());
            var b = a.withMinute(0).withSecond(0).withNano(0);

            assertTrue(TimeUtils.isRoughlyEqual(a, b));
        }

        @Test
        void GivenTruncationBeforeUpTo_WhenIsRoughlyEqual_ThenFalse() {
            var a = ZonedDateTime.of(2026, 3, 14, 18, 12, 9, 0, ZoneId.systemDefault());
            var b = a.withMinute(0).withSecond(0).withNano(0);

            assertFalse(TimeUtils.isRoughlyEqual(a, b, ChronoField.SECOND_OF_MINUTE));
        }
    }

    @Nested
    class TruncationTests {
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
    }
}
