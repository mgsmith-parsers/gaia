package tools.pantheum.gaia.gs1.util;

/**
 * Utility for rendering non-printable ASCII control characters (0–32) as
 * human-readable {@code {SYMBOL}} tokens.
 *
 * <p>Example: {@code char(29)} (GS1 FNC1 separator) renders as {@code {GS}}.</p>
 */
public final class ASCIIRevealerUtils {

    private ASCIIRevealerUtils() {}

    /** Standard names for ASCII characters 0–32, indexed by ASCII value. */
    private static final String[] SYMBOLS = {
        "NUL",   // 0  Null
        "SOH",   // 1  Start of Heading
        "STX",   // 2  Start of Text
        "ETX",   // 3  End of Text
        "EOT",   // 4  End of Transmission
        "ENQ",   // 5  Enquiry
        "ACK",   // 6  Acknowledge
        "BEL",   // 7  Bell
        "BS",    // 8  Backspace
        "HT",    // 9  Horizontal Tab
        "LF",    // 10 Line Feed
        "VT",    // 11 Vertical Tab
        "FF",    // 12 Form Feed
        "CR",    // 13 Carriage Return
        "SO",    // 14 Shift Out
        "SI",    // 15 Shift In
        "DLE",   // 16 Data Link Escape
        "DC1",   // 17 Device Control 1 (XON)
        "DC2",   // 18 Device Control 2
        "DC3",   // 19 Device Control 3 (XOFF)
        "DC4",   // 20 Device Control 4
        "NAK",   // 21 Negative Acknowledge
        "SYN",   // 22 Synchronous Idle
        "ETB",   // 23 End of Transmission Block
        "CAN",   // 24 Cancel
        "EM",    // 25 End of Medium
        "SUB",   // 26 Substitute
        "ESC",   // 27 Escape
        "FS",    // 28 File Separator
        "GS",    // 29 Group Separator  ← GS1 FNC1 separator
        "RS",    // 30 Record Separator
        "US",    // 31 Unit Separator
        "SP",    // 32 Space
    };

    /**
     * Returns the standard name for the given ASCII control character.
     *
     * @param c a character with ASCII value 0–32
     * @return the symbol name, e.g. {@code "GS"} for char 29
     * @throws IllegalArgumentException if {@code c} is outside the 0–32 range
     */
    public static String nameOf(char c) {
        if (c > 32) throw new IllegalArgumentException(
                "Character " + (int) c + " is not a control character (0-32)");
        return SYMBOLS[c];
    }

    /**
     * Replaces every ASCII control character (0–32) in the given string with
     * its {@code {SYMBOL}} token. All other characters are passed through unchanged.
     *
     * @param s the string to make printable; {@code null} returns an empty string
     * @return a printable representation of {@code s}
     */
    public static String printable(String s) {
        if (s == null) return "";
        var sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c <= 32) {
                sb.append('{').append(SYMBOLS[c]).append('}');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
