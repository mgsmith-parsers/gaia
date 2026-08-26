package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link LuhnUtils} — the Luhn (modulo-10) check used by IMEI (8040/8041) and EID (8042). */
@DisplayName("LuhnUtils")
class LuhnUtilsTest {

    @ParameterizedTest
    @DisplayName("isValid accepts Luhn-correct values")
    @ValueSource(strings = {
            "490154203237518",                  // IMEI
            "356938035643809",                  // IMEI
            "89044030050088826003380898765430", // EID
            "0",                                 // trivial single-digit checksum
    })
    void accepts(String value) {
        assertTrue(LuhnUtils.isValid(value));
    }

    @ParameterizedTest
    @DisplayName("isValid rejects Luhn-incorrect values")
    @ValueSource(strings = {
            "490154203237519",                  // IMEI check digit off by one
            "89044030050088826003380898765431", // EID check digit off by one
            "111111111111111",                  // fifteen 1s (sum 22)
    })
    void rejects(String value) {
        assertFalse(LuhnUtils.isValid(value));
    }

    @Test
    @DisplayName("isValid rejects null, empty and non-digit input")
    void rejectsMalformed() {
        assertFalse(LuhnUtils.isValid(null));
        assertFalse(LuhnUtils.isValid(""));
        assertFalse(LuhnUtils.isValid("4901542032375A8"));
    }

    @Test
    @DisplayName("calculateCheckDigit produces a digit that makes the value valid")
    void calculateRoundTrips() {
        String body = "89044030050088826003380898765430".substring(0, 31); // EID without its check digit
        int cd = LuhnUtils.calculateCheckDigit(body);
        assertEquals(0, cd);
        assertTrue(LuhnUtils.isValid(body + cd));

        assertEquals(8, LuhnUtils.calculateCheckDigit("49015420323751")); // IMEI body → 8
        assertTrue(LuhnUtils.isValid("49015420323751" + LuhnUtils.calculateCheckDigit("49015420323751")));
    }

    @Test
    @DisplayName("calculateCheckDigit returns -1 for null, empty and non-digit input")
    void calculateRejectsMalformed() {
        assertEquals(-1, LuhnUtils.calculateCheckDigit(null));
        assertEquals(-1, LuhnUtils.calculateCheckDigit(""));
        assertEquals(-1, LuhnUtils.calculateCheckDigit("12A45"));
    }
}
