package tools.pantheum.gaia.gs1.util;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

/**
 * Shared helpers for working with GS1 data values.
 *
 * <h2>Embedded GTIN type detection</h2>
 * <p>All GTINs are stored as 14 digits. The number of leading zeros reveals the
 * original format and determines where the GS1 company prefix begins:
 * <pre>
 *   000000XXXXXXXX  →  GTIN-8   (6 leading zeros, prefix at index 6)
 *   00XXXXXXXXXXXX  →  GTIN-12  (2 leading zeros, prefix at index 2)
 *   0XXXXXXXXXXXXX  →  GTIN-13  (1 leading zero,  prefix at index 1)
 *   1–9XXXXXXXXXXX  →  GTIN-14  (indicator digit,  prefix at index 1)
 * </pre>
 *
 * <h2>Check digit / check character pair verification</h2>
 * <p>Implements the two GS1 algorithms: the standard modulo-10 check digit
 * (spec §7.9.1, {@link #verifyModulo10(String)}) and the MOD 1021,32
 * alphanumeric check character pair (spec §7.9.5, {@link #verifyMod102132(String)}).
 */
public final class GS1Utils {

    private GS1Utils() {}

    /**
     * Detects the native GTIN format from a 14-digit GTIN value by counting
     * leading zeros.
     *
     * @param gtin14 the 14-digit GTIN string
     * @return {@code "GTIN-8"}, {@code "GTIN-12"}, {@code "GTIN-13"}, or
     *         {@code "GTIN-14"}; returns {@code "GTIN-14"} for any non-padded value
     */
    public static String detectFormat(String gtin14) {
        if (gtin14 == null || gtin14.length() != 14) return GS1Constants.GTIN_14;
        if (gtin14.startsWith(GS1Constants.GTIN_8_PADDING))  return GS1Constants.GTIN_8;
        if (gtin14.startsWith(GS1Constants.GTIN_12_PADDING)) return GS1Constants.GTIN_12;
        if (gtin14.startsWith(GS1Constants.GTIN_13_PADDING)) return GS1Constants.GTIN_13;
        return GS1Constants.GTIN_14;
    }

    /**
     * Returns the native GTIN value stripped of leading-zero padding.
     * e.g. {@code "00950600013435"} (GTIN-12) → {@code "950600013435"}
     *
     * @param gtin14 the 14-digit GTIN string
     * @return the native GTIN value (8, 12, 13, or 14 digits)
     */
    public static String nativeValue(String gtin14) {
        if (gtin14 == null || gtin14.length() != 14) return gtin14;
        if (gtin14.startsWith(GS1Constants.GTIN_8_PADDING))  return gtin14.substring(GS1Constants.GTIN_8_PREFIX_OFFSET);
        if (gtin14.startsWith(GS1Constants.GTIN_12_PADDING)) return gtin14.substring(GS1Constants.GTIN_12_PREFIX_OFFSET);
        if (gtin14.startsWith(GS1Constants.GTIN_13_PADDING)) return gtin14.substring(GS1Constants.GTIN_13_14_PREFIX_OFFSET);
        return gtin14;
    }

    // -------------------------------------------------------------------------
    // Check digit / check character pair data tables
    // -------------------------------------------------------------------------

    /*
     * Inverse map: character → reference value for the check-character calculation.
     */
    private static final int[] CHAR_TO_REF_VALUE = buildCharToRefValue();

    /*
     * Check character set (spec Table 7-19): maps reference values 0–31 to their
     * corresponding output character.
     */
    private static final char[] CHECK_CHAR_SET = {
        '2','3','4','5','6','7','8','9',
        'A','B','C','D','E','F','G','H',
        'J','K','L','M','N','P','Q','R',
        'S','T','U','V','W','X','Y','Z'
    };

    /* First 30 prime numbers used as weights in the MOD 1021,32 algorithm. */
    private static final int[] PRIMES = {
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
        73, 79, 83, 89, 97,101,103,107,109,113
    };

    // -------------------------------------------------------------------------
    // Standard modulo-10 check digit (GS1 spec §7.9.1)
    // -------------------------------------------------------------------------

    /**
     * Calculates the standard GS1 modulo-10 check digit for a string of data digits
     * (the value <em>without</em> its check digit).
     *
     * Algorithm: multiply data digits by alternating weights 3 and 1, starting with
     * weight 3 at the rightmost data digit and working leftward. Sum the products;
     * check digit = (10 - sum%10) % 10.
     *
     * @param data the data digits only (no check digit); must be non-empty and all decimal digits
     * @return the check digit {@code 0}–{@code 9}, or {@code -1} if {@code data} is
     *         {@code null}, empty, or contains a non-digit character
     */
    public static int calculateCheckDigit(String data) {
        if (data == null || data.isEmpty()) return -1;
        int sum = 0;
        int n = data.length();
        for (int i = 0; i < n; i++) {
            char c = data.charAt(i);
            if (c < '0' || c > '9') return -1;
            int digit = c - '0';
            // Position from right (1-based): rightmost data digit = position 1 → weight 3.
            int posFromRight = n - i;
            int weight = (posFromRight % 2 == 1) ? 3 : 1;
            sum += digit * weight;
        }
        return (10 - (sum % 10)) % 10;
    }

    /**
     * Verifies the standard GS1 modulo-10 check digit: the last character must equal
     * {@link #calculateCheckDigit(String)} of the preceding data digits.
     *
     * @param value complete numeric string including the check digit as the last character
     * @return a new {@code boolean}
     */
    public static boolean verifyModulo10(String value) {
        if (value == null || value.length() < 2) return false;
        char checkChar = value.charAt(value.length() - 1);
        if (checkChar < '0' || checkChar > '9') return false;
        int expected = calculateCheckDigit(value.substring(0, value.length() - 1));
        return expected >= 0 && expected == (checkChar - '0');
    }

    // -------------------------------------------------------------------------
    // MOD 1021,32 check character pair (GS1 spec §7.9.5)
    // -------------------------------------------------------------------------

    /**
     * Verifies the GS1 MOD 1021,32 alphanumeric check character pair.
     * The last two characters of {@code value} are the check character pair.
     *
     * @param value complete alphanumeric string including the two check characters
     * @return a new {@code boolean}
     */
    public static boolean verifyMod102132(String value) {
        if (value == null || value.length() < 3) return false;

        String data       = value.substring(0, value.length() - 2);
        char   c1Expected = value.charAt(value.length() - 2);
        char   c2Expected = value.charAt(value.length() - 1);

        int[] calculated = calculateCheckCharPair(data);
        if (calculated == null) return false;

        return c1Expected == CHECK_CHAR_SET[calculated[0]]
            && c2Expected == CHECK_CHAR_SET[calculated[1]];
    }

    /**
     * Calculates the MOD 1021,32 check character pair reference values for the given data string.
     *
     * @return int[2] = {C1 ref value, C2 ref value}, or null if the data contains
     *         characters not in the GS1 AI encodable character set.
     */
    private static int[] calculateCheckCharPair(String data) {
        if (data.isEmpty() || data.length() > PRIMES.length) return null;

        long sum = 0;
        int n = data.length();
        for (int i = 0; i < n; i++) {
            char c = data.charAt(i);
            if (c >= CHAR_TO_REF_VALUE.length) return null; // non-ASCII character not in set
            int refValue = CHAR_TO_REF_VALUE[c];
            if (refValue < 0) return null; // ASCII character not in GS1 character set 82
            // Weight: prime numbers assigned right-to-left (rightmost = prime[0] = 2)
            int weight = PRIMES[n - 1 - i];
            sum += (long) refValue * weight;
        }

        int ck = (int)(sum % 1021);
        int c1 = ck / 32;
        int c2 = ck % 32;
        return new int[]{c1, c2};
    }

    // -------------------------------------------------------------------------
    // Build lookup table at class initialisation time
    // -------------------------------------------------------------------------

    private static int[] buildCharToRefValue() {
        int[] table = new int[128];
        java.util.Arrays.fill(table, -1);
        // Manually map each character to its reference value per Table 7-18
        table['!']  =  0; table['"']  =  1; table['%']  =  2;
        table['&']  =  3; table['\''] =  4; table['(']  =  5;
        table[')']  =  6; table['*']  =  7; table['+']  =  8;
        table[',']  =  9; table['-']  = 10; table['.']  = 11;
        table['/']  = 12;
        table['0']  = 13; table['1']  = 14; table['2']  = 15;
        table['3']  = 16; table['4']  = 17; table['5']  = 18;
        table['6']  = 19; table['7']  = 20; table['8']  = 21;
        table['9']  = 22;
        table[':']  = 23; table[';']  = 24; table['<']  = 25;
        table['=']  = 26; table['>']  = 27; table['?']  = 28;
        table['A']  = 29; table['B']  = 30; table['C']  = 31;
        table['D']  = 32; table['E']  = 33; table['F']  = 34;
        table['G']  = 35; table['H']  = 36; table['I']  = 37;
        table['J']  = 38; table['K']  = 39; table['L']  = 40;
        table['M']  = 41; table['N']  = 42; table['O']  = 43;
        table['P']  = 44; table['Q']  = 45; table['R']  = 46;
        table['S']  = 47; table['T']  = 48; table['U']  = 49;
        table['V']  = 50; table['W']  = 51; table['X']  = 52;
        table['Y']  = 53; table['Z']  = 54; table['_']  = 55;
        table['a']  = 56; table['b']  = 57; table['c']  = 58;
        table['d']  = 59; table['e']  = 60; table['f']  = 61;
        table['g']  = 62; table['h']  = 63; table['i']  = 64;
        table['j']  = 65; table['k']  = 66; table['l']  = 67;
        table['m']  = 68; table['n']  = 69; table['o']  = 70;
        table['p']  = 71; table['q']  = 72; table['r']  = 73;
        table['s']  = 74; table['t']  = 75; table['u']  = 76;
        table['v']  = 77; table['w']  = 78; table['x']  = 79;
        table['y']  = 80; table['z']  = 81;
        return table;
    }
}
