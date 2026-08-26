package tools.pantheum.gaia;

import tools.pantheum.gaia.gs1.util.GS1Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckDigitCharacterValidatorTest {

    // -------------------------------------------------------------------------
    // Standard modulo-10 check digit (§7.9.1)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Modulo-10: valid GTIN-14")
    void modulo10ValidGtin14() {
        assertTrue(GS1Utils.verifyModulo10("09506000134352"));
    }

    @Test
    @DisplayName("Modulo-10: invalid GTIN-14 (wrong check digit)")
    void modulo10InvalidGtin14() {
        assertFalse(GS1Utils.verifyModulo10("09506000134350"));
    }

    @Test
    @DisplayName("Modulo-10: valid SSCC (spec §7.9.1 example)")
    void modulo10ValidSscc() {
        // Example from Table 7-9: data=37610425002123456, check digit=9
        assertTrue(GS1Utils.verifyModulo10("376104250021234569"));
    }

    @Test
    @DisplayName("Modulo-10: GTIN-8 with correct check digit")
    void modulo10Gtin8() {
        // GTIN-8: 96385074 — check digit 4
        assertTrue(GS1Utils.verifyModulo10("96385074"));
    }

    @Test
    @DisplayName("Modulo-10: too short returns false")
    void modulo10TooShort() {
        assertFalse(GS1Utils.verifyModulo10("1"));
    }

    @Test
    @DisplayName("Modulo-10: non-digit character returns false")
    void modulo10NonDigit() {
        assertFalse(GS1Utils.verifyModulo10("0950600013A352"));
    }

    // -------------------------------------------------------------------------
    // Modulo-10 check digit calculation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("calculateCheckDigit: GTIN-14 data yields the expected check digit")
    void calcGtin14() {
        assertEquals(2, GS1Utils.calculateCheckDigit("0950600013435"));
    }

    @Test
    @DisplayName("calculateCheckDigit: SSCC data (spec §7.9.1 example) yields 9")
    void calcSscc() {
        assertEquals(9, GS1Utils.calculateCheckDigit("37610425002123456"));
    }

    @Test
    @DisplayName("calculateCheckDigit: GTIN-8 data yields 4")
    void calcGtin8() {
        assertEquals(4, GS1Utils.calculateCheckDigit("9638507"));
    }

    @Test
    @DisplayName("calculateCheckDigit: appended digit round-trips through verifyModulo10")
    void calcRoundTrip() {
        String data = "37610425002123456";
        int cd = GS1Utils.calculateCheckDigit(data);
        assertTrue(GS1Utils.verifyModulo10(data + cd));
    }

    @Test
    @DisplayName("calculateCheckDigit: null, empty, or non-digit returns -1")
    void calcInvalid() {
        assertEquals(-1, GS1Utils.calculateCheckDigit(null));
        assertEquals(-1, GS1Utils.calculateCheckDigit(""));
        assertEquals(-1, GS1Utils.calculateCheckDigit("12A4"));
    }

    // -------------------------------------------------------------------------
    // MOD 1021,32 check character pair (§7.9.5)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("MOD 1021,32: valid GMN from spec §7.9.5 example")
    void mod102132ValidGmn() {
        // Spec example: GMN = 1987654Ad4X4bL5ttr2310c2K (25 chars), check pair = 2K
        assertTrue(GS1Utils.verifyMod102132("1987654Ad4X4bL5ttr2310c2K"));
    }

    @Test
    @DisplayName("MOD 1021,32: wrong check character returns false")
    void mod102132WrongCheck() {
        // Change check pair from 2K to 2X
        assertFalse(GS1Utils.verifyMod102132("1987654Ad4X4bL5ttr2310c2X"));
    }

    @Test
    @DisplayName("MOD 1021,32: too short returns false")
    void mod102132TooShort() {
        assertFalse(GS1Utils.verifyMod102132("A2"));
    }
}
