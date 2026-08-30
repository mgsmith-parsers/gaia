package tools.pantheum.gaia.gs1.charset;

/**
 * The GS1-defined character sets used to validate AI data.
 *
 * <p>Character counts and members follow GS1 General Specifications Table 7-11.
 */
public enum GS1CharacterSet {

    /** Digits {@code 0–9} only. */
    NUMERIC(
            "0123456789"),

    /**
     * GS1 AI encodable character set 39.
     * Digits {@code 0–9}, uppercase {@code A–Z}, and {@code # - /}
     * (39 characters total).
     */
    CSET39(
            "0123456789" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "#-/"),

    /**
     * GS1 AI encodable character set 64.
     * Digits {@code 0–9}, upper- and lower-case letters, {@code -}, {@code _}, and {@code =}
     * (64 characters total).
     */
    CSET64(
            "0123456789" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "-_="),

    /**
     * GS1 AI encodable character set 82.
     * Digits, upper- and lower-case letters, and the 20 permitted special
     * characters {@code !"&'()*+,-./:;<=>?_%} (82 characters total).
     */
    CSET82(
            "0123456789" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "!\"%&'()*+,-./:;<=>?_");

    private final String chars;
    private final boolean[] membership = new boolean[128];

    GS1CharacterSet(String chars) {
        this.chars = chars;
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (c < 128) membership[c] = true;
        }
    }

    /**
     * Returns the complete string of characters permitted in this set.
     *
     * @return the chars.
     */
    public String getChars() {
        return chars;
    }

    /**
     * Returns {@code true} if {@code c} is a member of this character set.
     *
     * @param c the c
     * @return {@code true} if contains.
     */
    public boolean contains(char c) {
        return c < 128 && membership[c];
    }

    /**
     * Maps an AI component type code and the AI-level format string to the appropriate
     * {@link GS1CharacterSet} for character-level validation.
     *
     * <p>For alphanumeric ({@code "X"}) components the format string narrows the set:
     * <ul>
     *   <li>{@code "N"} → {@link #NUMERIC}</li>
     *   <li>{@code "X"} + {@code formatString} contains {@code "Z"} → {@link #CSET64}</li>
     *   <li>{@code "X"} + {@code formatString} contains {@code "Y"} → {@link #CSET39}</li>
     *   <li>{@code "X"} otherwise → {@link #CSET82}</li>
     * </ul>
     *
     * @param type         the component type code ({@code "N"} or {@code "X"})
     * @param formatString the AI-level format string (e.g. {@code "X..20"}), may be {@code null}
     * @return the character set to validate against, or {@code null} to skip charset checking
     */
    public static GS1CharacterSet forComponentType(String type, String formatString) {
        if ("N".equals(type)) return NUMERIC;
        if ("X".equals(type)) {
            if (formatString != null && formatString.contains("Z")) return CSET64;
            if (formatString != null && formatString.contains("Y")) return CSET39;
            return CSET82;
        }
        return null; // unknown type — fail open
    }
}
