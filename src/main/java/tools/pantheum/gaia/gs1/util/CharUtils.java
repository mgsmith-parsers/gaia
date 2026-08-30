package tools.pantheum.gaia.gs1.util;

/**
 * Character classification utilities.
 */
public final class CharUtils {

    private CharUtils() {}

    /**
     * Is upper alpha.
     *
     * @param c the c
     * @return a new {@code boolean}
     */
    public static boolean isUpperAlpha(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Is digit.
     *
     * @param c the c
     * @return a new {@code boolean}
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * RFC 3986 unreserved characters: A–Z a–z 0–9 - _ . ~
     *
     * @param c the c
     * @return a new {@code boolean}
     */
    public static boolean isUnreservedPcenc(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || c == '-' || c == '_' || c == '.' || c == '~';
    }

    /**
     * Is hex digit.
     *
     * @param c the c
     * @return a new {@code boolean}
     */
    public static boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }
}
