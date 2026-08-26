package tools.pantheum.gaia.gs1.util;

/**
 * String utility methods.
 */
public final class StringUtils {

    private StringUtils() {}

    /** Returns {@code true} if every character in {@code value} is {@code '0'}. */
    public static boolean isAllZeros(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0') return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if {@code value} is non-{@code null}, non-empty, and every
     * character is a decimal digit.
     */
    public static boolean isAllDigits(String value) {
        if (value == null || value.isEmpty()) return false;
        for (int i = 0; i < value.length(); i++) {
            if (!CharUtils.isDigit(value.charAt(i))) return false;
        }
        return true;
    }
}
