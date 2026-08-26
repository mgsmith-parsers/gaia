package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GDTIValidator} via AI 253 (GDTI).
 *
 * <p>GDTI structure: [GS1 Company Prefix][Document Type][Check digit (1)] (13 digits, mandatory)
 * + [Serial reference (0–17 alphanumeric chars, optional)].
 * The GS1 prefix starts at index 0 of the 13-digit key component.
 *
 * <h3>GS1 prefix reference</h3>
 * <pre>
 *   930  → GS1 Australia   (valid tests)
 *   140  → unassigned gap  (unknown-prefix boundary tests)
 * </pre>
 */
@DisplayName("GDTIValidator — AI 253")
class GDTIValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid GDTI key only: prefix 930 (GS1 Australia)")
    void validGdtiKeyOnly() {
        // "9300000000002" — prefix "930", check digit 2
        assertTrue(valid("253" + "9300000000002"));
    }

    @Test
    @DisplayName("Valid GDTI with optional serial suffix")
    void validGdtiWithSerial() {
        // Key "9300000000002" + serial "SERIAL01"
        assertTrue(valid("253" + "9300000000002" + "SERIAL01"));
    }

    // -------------------------------------------------------------------------
    // Invalid — all zeros boundary
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All-zeros GDTI key: structurally valid, fails all-zeros rule")
    void allZerosKey() {
        // "0000000000000" — 13 zeros; check digit 0 is mathematically correct
        ParseResult resp = parser.parse("253" + "0000000000000");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "must not be all zeros");
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GDTI key with unknown prefix 140: is advisory (warning)")
    void unknownPrefix() {
        // "1400000000007" — prefix "140" not in registry, check digit 7
        ParseResult resp = parser.parse("253" + "1400000000007");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    @Test
    @DisplayName("GDTI with unknown prefix and serial: prefix check still fails")
    void unknownPrefixWithSerial() {
        ParseResult resp = parser.parse("253" + "1400000000007" + "SERIAL01");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean valid(String element) {
        ParseResult resp = parser.parse(element);
        assertTrue(resp.isValid(), () -> "Expected valid but got errors: " + resp.getErrors());
        return true;
    }

    private void assertErrorContains(ParseResult resp, String fragment) {
        assertTrue(resp.getErrors().stream()
                        .anyMatch(e -> e.getMessage().contains(fragment)),
                () -> "Expected error containing '" + fragment + "' but got: " + resp.getErrors());
    }

    private void assertWarningContains(ParseResult resp, String fragment) {
        assertTrue(resp.getWarnings().stream()
                        .anyMatch(e -> e.getMessage().contains(fragment)),
                () -> "Expected warning containing '" + fragment + "' but got: " + resp.getWarnings());
    }
}
