package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SSCCValidator} via AI 00 (SSCC).
 *
 * <p>SSCC structure: [Extension digit (1)][GS1 Company Prefix + Serial][Check digit (1)] = 18 digits.
 * The GS1 prefix check skips the leading extension digit.
 *
 * <h3>GS1 prefix reference (applied after stripping extension digit)</h3>
 * <pre>
 *   761  → GS1 Switzerland   (extension "3", prefix "761")
 *   140  → unassigned gap    (extension "1", prefix "140")
 * </pre>
 */
@DisplayName("SSCCValidator — AI 00")
class SSCCValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid SSCC: extension 3, prefix 761 (GS1 Switzerland)")
    void validSscc() {
        // "376104250021234569" — from GS1 spec Table 7-9; ext=3, after-ext prefix=761
        assertTrue(valid(GS1Constants_AICodes.AI_00_SSCC + "376104250021234569"));
    }

    // -------------------------------------------------------------------------
    // Invalid — all zeros boundary
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All-zeros SSCC: structurally valid check digit, fails all-zeros rule")
    void allZeros() {
        // 18 zeros — check digit 0 is mathematically correct
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_00_SSCC + "000000000000000000");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "must not be all zeros");
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Unknown prefix 140 (after skipping extension digit): is advisory (warning)")
    void unknownPrefix() {
        // "114000000000000004" — ext="1", after-ext prefix="140" not in registry
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_00_SSCC + "114000000000000004");
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
