package tools.pantheum.gaia.gs1.util;

import java.math.BigInteger;

/**
 * Utility methods for IBAN validation.
 */
public final class IBANUtils {

    private IBANUtils() {}

    /**
     * Verifies the IBAN MOD-97 check digit (ISO 7064 MOD 97-10).
     * Moves the first 4 characters to the end, converts letters to digits (A=10…Z=35),
     * and checks that the result mod 97 equals 1.
     * Returns {@code false} for null, too-short, or otherwise malformed input.
     */
    public static boolean verifyIbanMod97(String iban) {
        if (iban == null || iban.length() < 5) return false;

        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numeric.append(Character.toUpperCase(c) - 'A' + 10);
            } else {
                numeric.append(c);
            }
        }

        try {
            return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97))
                    .equals(BigInteger.ONE);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
