package tools.pantheum.gaia.gs1.util;

import java.time.Year;

/**
 * Date utility methods for GS1 two-digit and four-digit calendar date calculations.
 */
public final class DateUtils {

    /**
     * The reference year for GS1 sliding-window year resolution, fixed at JVM startup.
     * Caching avoids repeated wall-clock reads and ensures deterministic results
     * within a single JVM run (e.g. batch jobs cannot straddle a year boundary).
     */
    private static final int REFERENCE_YEAR = Year.now().getValue();

    /**
     * Resolves a 2-digit year to a full 4-digit year using the GS1 sliding-window rule:
     * the result must be within 49 years in the past and 50 years in the future of the
     * JVM startup year. (GS1 General Specifications section 7.12)
     */
    public static int resolveYear(int yy) {
        int century   = (REFERENCE_YEAR / 100) * 100;
        int candidate = century + yy;
        if (candidate > REFERENCE_YEAR + 50) candidate -= 100;
        if (candidate < REFERENCE_YEAR - 49) candidate += 100;
        return candidate;
    }

    private DateUtils() {}

    /**
     * Maximum day for a 2-digit GS1 year and 1-based month.
     * The year is resolved via the GS1 sliding-window rule (same as {@link #resolveYear}).
     */
    public static int maxDayYy(int yy, int mm) {
        return maxDayFull(resolveYear(yy), mm);
    }

    /** Maximum day for the given full year and 1-based month. */
    public static int maxDayFull(int year, int mm) {
        switch (mm) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: return 31;
            case 4: case 6: case 9: case 11:                           return 30;
            case 2: return isLeapYear(year) ? 29 : 28;
            default: return 31;
        }
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

}
