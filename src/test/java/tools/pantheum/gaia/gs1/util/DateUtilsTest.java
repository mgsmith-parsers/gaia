package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link DateUtils} — GS1 two-digit year and month-length rules. */
@DisplayName("DateUtils")
class DateUtilsTest {

    @Test
    @DisplayName("resolveYear maps the current two-digit year to the current year")
    void resolveCurrentYear() {
        int now = LocalDate.now().getYear();
        assertEquals(now, DateUtils.resolveYear(now % 100));
    }

    @Test
    @DisplayName("isLeapYear follows Gregorian rules")
    void leapYears() {
        assertTrue(DateUtils.isLeapYear(2024));
        assertTrue(DateUtils.isLeapYear(2000));
        assertFalse(DateUtils.isLeapYear(1900));
        assertFalse(DateUtils.isLeapYear(2026));
    }

    @Test
    @DisplayName("maxDayFull respects month lengths and leap years")
    void maxDayFull() {
        assertEquals(31, DateUtils.maxDayFull(2026, 1));
        assertEquals(28, DateUtils.maxDayFull(2026, 2));
        assertEquals(29, DateUtils.maxDayFull(2024, 2));
        assertEquals(30, DateUtils.maxDayFull(2026, 4));
    }

    @Test
    @DisplayName("maxDayYy resolves the two-digit year before applying month rules")
    void maxDayYy() {
        int yy = LocalDate.now().getYear() % 100;
        assertEquals(DateUtils.maxDayFull(DateUtils.resolveYear(yy), 2),
                DateUtils.maxDayYy(yy, 2));
    }
}
