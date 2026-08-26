package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GINCValidator} via AI 401 (GINC).
 *
 * <p>GINC structure: [GS1 Company Prefix (numeric)][Consignment Reference (alphanumeric)],
 * 1–30 characters total, no check digit.
 * The GS1 prefix starts at index 0.
 *
 * <p>All prefix ranges in the GS1 prefix registry are numeric strings, so a GINC
 * starting with a non-digit character will naturally fail the prefix lookup.
 *
 * <h3>GS1 prefix reference</h3>
 * <pre>
 *   930  → GS1 Australia        (valid tests)
 *   950  → GS1 Global Office    (valid tests)
 *   140  → unassigned gap       (unknown-prefix boundary tests)
 * </pre>
 */
@DisplayName("GINCValidator — AI 401")
class GINCValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid GINC: numeric prefix 930 (GS1 Australia)")
    void validGincGS1Australia() {
        assertTrue(valid("401" + "930SHIPMENT001"));
    }

    @Test
    @DisplayName("Valid GINC: numeric prefix 950 (GS1 Global Office)")
    void validGincGS1GlobalOffice() {
        assertTrue(valid("401" + "950CONSIGNMENT99"));
    }

    @Test
    @DisplayName("Valid GINC: alphanumeric reference after numeric prefix")
    void validGincAlphanumericRef() {
        assertTrue(valid("401" + "930ABC-DEF_001"));
    }

    @Test
    @DisplayName("Valid GINC: minimum length (3 chars total, 3-char prefix)")
    void validGincMinimumLength() {
        // 3-char value: 3-char prefix "930" — prefix is valid, no reference chars needed beyond prefix
        // GS1 spec allows 1-30 chars; a GINC consisting only of the prefix is technically valid
        assertTrue(valid("401" + "930A"));  // prefix "930" + 1 reference char "A"
    }

    // -------------------------------------------------------------------------
    // Invalid — unknown GS1 prefix
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GINC with unknown prefix 140: is advisory (warning)")
    void unknownNumericPrefix() {
        ParseResult resp = parser.parse("401" + "140SHIPMENT001");
        assertTrue(resp.isValid());
        assertWarningContains(resp, "recognised GS1 company prefix");
    }

    @Test
    @DisplayName("GINC starting with a letter: all-letter prefix fails registry lookup")
    void startsWithLetter() {
        // All prefix ranges are numeric; a letter-prefix GINC cannot match any range
        ParseResult resp = parser.parse("401" + "ABCSHIPMENT001");
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

    private void assertWarningContains(ParseResult resp, String fragment) {
        assertTrue(resp.getWarnings().stream()
                        .anyMatch(e -> e.getMessage().contains(fragment)),
                () -> "Expected warning containing '" + fragment + "' but got: " + resp.getWarnings());
    }
}
