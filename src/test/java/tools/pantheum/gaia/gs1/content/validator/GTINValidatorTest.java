package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GTINValidator} via AI 01 (GTIN).
 *
 * <p>All values carry correct modulo-10 check digits so the standard pipeline
 * passes and the custom validator is exercised.
 *
 * <h3>GS1 prefix reference</h3>
 * <pre>
 *   950  → GS1 Global Office      (valid GTIN-13 embedded tests)
 *   930  → GS1 Australia          (valid GTIN-12/GTIN-14 tests)
 *   963  → GS1 Global GTIN-8      (valid GTIN-8 embedded tests)
 *   140  → unassigned gap         (unknown-prefix boundary tests)
 * </pre>
 */
@DisplayName("GTINValidator — AI 01")
class GTINValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid — happy paths for all four GTIN lengths
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GTIN-13 embedded (1 leading zero): valid check digit, prefix 950")
    void validGtin13() {
        // strip "0" → "9506000134352", prefix "950" = GS1 Global Office
        assertTrue(valid(GS1Constants_AICodes.AI_01_GTIN + "09506000134352"));
    }

    @Test
    @DisplayName("GTIN-12 embedded (2 leading zeros): valid check digit, prefix 930")
    void validGtin12() {
        // strip "00" → "930000000208", prefix "930" = GS1 Australia
        assertTrue(valid(GS1Constants_AICodes.AI_01_GTIN + "00930000000208"));
    }

    @Test
    @DisplayName("GTIN-8 embedded (6 leading zeros): valid check digit, prefix 963")
    void validGtin8() {
        // strip "000000" → "96385074", prefix "963" = GS1 Global GTIN-8
        assertTrue(valid(GS1Constants_AICodes.AI_01_GTIN + "00000096385074"));
    }

    @Test
    @DisplayName("GTIN-14 (indicator digit 1): valid check digit, prefix 930")
    void validGtin14() {
        // strip indicator "1" → "9300000000023", prefix "930" = GS1 Australia
        assertTrue(valid(GS1Constants_AICodes.AI_01_GTIN + "19300000000023"));
    }

    // -------------------------------------------------------------------------
    // Invalid — all zeros boundary
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All-zeros GTIN-14: structurally valid check digit, fails all-zeros rule")
    void allZeros() {
        // "00000000000000" — 14 zeros, check digit 0 is mathematically correct
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "00000000000000");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "must not be all zeros");
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GTIN-13 embedded: unknown prefix 140 is advisory (warning)")
    void unknownPrefixGtin13() {
        // strip "0" → "1400000000007", prefix "140" not in registry
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "01400000000007");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    @Test
    @DisplayName("GTIN-12 embedded: unknown prefix 140 is advisory (warning)")
    void unknownPrefixGtin12() {
        // strip "00" → "140000000003", prefix "140" not in registry
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "00140000000003");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    @Test
    @DisplayName("GTIN-8 embedded: unknown prefix 140 is advisory (warning)")
    void unknownPrefixGtin8() {
        // strip "000000" → "14000072", prefix "140" not in registry
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "00000014000072");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    @Test
    @DisplayName("GTIN-14: unknown prefix 140 (after stripping indicator) is advisory (warning)")
    void unknownPrefixGtin14() {
        // strip indicator "1" → "1400000000004", prefix "140" not in registry
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "11400000000004");
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
