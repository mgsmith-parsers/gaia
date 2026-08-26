package tools.pantheum.gaia.gs1.util;

/**
 * The Luhn (modulo-10) check digit algorithm, as used by the telecom identifiers
 * carried in GS1 Application Identifiers 8040/8041 (IMEI) and 8042 (EID).
 *
 * <h2>Algorithm</h2>
 * Working right-to-left across the digits, every second digit is doubled; a doubled
 * result above 9 has 9 subtracted (equivalently, its digits are summed). The value is
 * valid when the total is a multiple of 10.
 *
 * <pre>
 *   4 9 0 1 5 4 2 0 3 2 3 7 5 1 8      IMEI, check digit 8 (rightmost)
 *   ↑   ↑   ↑   ↑   ↑   ↑   ↑          doubled (every second from the right)
 * </pre>
 *
 * <h2>Not the GS1 check digit</h2>
 * This is <strong>distinct</strong> from the GS1 modulo-10 check digit in
 * {@link GS1Utils#calculateCheckDigit(String)}, which applies alternating weights of
 * 3 and 1 rather than doubling, and does not fold results above 9. The two algorithms
 * are not interchangeable: GTIN, SSCC and the other GS1 keys use the GS1 routine,
 * while IMEI and EID use Luhn. Keeping them in separate classes makes picking the
 * wrong one a visible mistake at the call site.
 *
 * <p>Both methods reject a {@code null}, empty, or non-numeric argument rather than
 * throwing, mirroring {@link GS1Utils}.
 */
public final class LuhnUtils {

    private LuhnUtils() {}

    /**
     * Verifies a complete Luhn-protected value — the payload digits <em>including</em>
     * the trailing check digit.
     *
     * @param value the full value, e.g. a 15-digit IMEI or a 32-digit EID
     * @return {@code true} if every character is a decimal digit and the Luhn checksum
     *         is a multiple of 10; {@code false} if {@code value} is {@code null},
     *         empty, contains a non-digit, or fails the checksum
     */
    public static boolean isValid(String value) {
        if (value == null || value.isEmpty()) return false;

        int sum = 0;
        // The rightmost digit is the check digit itself, so doubling starts one place left.
        boolean doubling = false;
        for (int i = value.length() - 1; i >= 0; i--) {
            char c = value.charAt(i);
            if (!CharUtils.isDigit(c)) return false;

            int digit = c - '0';
            if (doubling) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            doubling = !doubling;
        }
        return sum % 10 == 0;
    }

    /**
     * Calculates the Luhn check digit for a string of data digits (the value
     * <em>without</em> its check digit).
     *
     * <p>Appending the returned digit to {@code data} yields a value that satisfies
     * {@link #isValid(String)}.
     *
     * @param data the data digits only (no check digit)
     * @return the check digit {@code 0}–{@code 9}, or {@code -1} if {@code data} is
     *         {@code null}, empty, or contains a non-digit character
     */
    public static int calculateCheckDigit(String data) {
        if (data == null || data.isEmpty()) return -1;

        int sum = 0;
        // With the check digit appended, the rightmost data digit shifts to position 2
        // from the right — the first doubled position.
        boolean doubling = true;
        for (int i = data.length() - 1; i >= 0; i--) {
            char c = data.charAt(i);
            if (!CharUtils.isDigit(c)) return -1;

            int digit = c - '0';
            if (doubling) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            doubling = !doubling;
        }
        return (10 - (sum % 10)) % 10;
    }
}
