package tools.pantheum.gaia.gs1.util;

/**
 * Integer parsing utilities.
 */
public final class IntUtils {

    private IntUtils() {}

    /** Parses a decimal integer from {@code s[start..end)}; returns -1 on failure. */
    public static int parseDigits(String s, int start, int end) {
        try {
            return Integer.parseInt(s.substring(start, end));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
