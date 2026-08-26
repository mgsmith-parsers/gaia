package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GSINValidator} via AI 402 (GSIN).
 *
 * <p>GSIN structure: [GS1 Company Prefix][Shipper's Reference][Check digit (1)] = 17 digits.
 * The GS1 prefix starts at index 0 (no leading indicator or extension digit).
 *
 * <h3>GS1 prefix reference</h3>
 * <pre>
 *   930  → GS1 Australia   (valid tests)
 *   140  → unassigned gap  (unknown-prefix boundary tests)
 * </pre>
 */
@DisplayName("GSINValidator — AI 402")
class GSINValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid GSIN: prefix 930 (GS1 Australia)")
    void validGsin() {
        // "93000000000000002" — prefix "930", check digit 2
        assertTrue(valid("402" + "93000000000000002"));
    }

    // -------------------------------------------------------------------------
    // Invalid — all zeros boundary
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All-zeros GSIN: structurally valid check digit, fails all-zeros rule")
    void allZeros() {
        // "00000000000000000" — 17 zeros; check digit 0 is mathematically correct
        ParseResult resp = parser.parse("402" + "00000000000000000");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "must not be all zeros");
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GSIN with unknown prefix 140: is advisory (warning)")
    void unknownPrefix() {
        // "14000000000000007" — prefix "140" not in registry, check digit 7
        ParseResult resp = parser.parse("402" + "14000000000000007");
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
