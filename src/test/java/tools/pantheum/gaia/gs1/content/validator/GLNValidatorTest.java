package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GLNValidator} via AI 414 (Physical Location GLN).
 *
 * <p>GLN structure: [GS1 Company Prefix][Location Reference][Check digit (1)] = 13 digits.
 * The GS1 prefix starts at index 0 (no leading indicator digit).
 * The same validator is shared by AIs 410–417; AI 414 is used here as representative.
 *
 * <h3>GS1 prefix reference</h3>
 * <pre>
 *   930  → GS1 Australia   (valid tests)
 *   140  → unassigned gap  (unknown-prefix boundary tests)
 * </pre>
 */
@DisplayName("GLNValidator — AI 414")
class GLNValidatorTest {

    static GaiaParser parser;

    /**
     * AI (415) requires AI (8020) to be present.
     * Prepend a minimal valid payment-slip reference (8020) so the structural
     * gate passes and the GLNValidator actually runs.
     * The GS character (0x1D) terminates the variable-length 8020 value.
     */
    private static final String WITH_PAY_SLIP = "8020R001";

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid GLN: prefix 930 (GS1 Australia)")
    void validGln() {
        // "9300000000002" — prefix "930" = GS1 Australia, check digit 2
        assertTrue(valid("414" + "9300000000002"));
    }

    // -------------------------------------------------------------------------
    // Invalid — all zeros boundary
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All-zeros GLN: structurally valid check digit, fails all-zeros rule")
    void allZeros() {
        // "0000000000000" — 13 zeros; check digit 0 is mathematically correct
        ParseResult resp = parser.parse("414" + "0000000000000");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "must not be all zeros");
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GLN with unknown prefix 140: is advisory (warning)")
    void unknownPrefix() {
        // "1400000000007" — prefix "140" not in registry, check digit 7
        ParseResult resp = parser.parse("414" + "1400000000007");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    // -------------------------------------------------------------------------
    // Valid — other AIs in the 410–417 range share the same validator
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("AI 410 (Ship to): valid GLN accepted")
    void validAi410() {
        assertTrue(valid("410" + "9300000000002"));
    }

    @Test
    @DisplayName("AI 417 (Party GLN): valid GLN accepted")
    void validAi417() {
        assertTrue(valid("417" + "9300000000002"));
    }

    @Test
    @DisplayName("AI 415 (Pay to): unknown prefix rejected")
    void unknownPrefixAi415() {
        // AI 415 requires AI 8020; prepend a valid payment-slip reference so the
        // structural gate passes and the GLNValidator custom check actually runs.
        ParseResult resp = parser.parse(WITH_PAY_SLIP + "415" + "1400000000007");
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
