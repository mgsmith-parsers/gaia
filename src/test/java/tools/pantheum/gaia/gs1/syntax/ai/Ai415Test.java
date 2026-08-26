package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AI (415) — PAY TO.
 *
 * <p>Global Location Number (GLN) of the invoicing party
 *
 * <p>Format: {@code N3+N13}
 *
 * <p>Required AIs: ["8020"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (415) — PAY TO")
class Ai415Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (415). */
    private static final String VALID_VALUE = "9506000134352";

    /** A fully valid element string, including the required AIs (8020). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_8020_REF_NO + "A"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352";

    @Test
    @DisplayName("AI (415) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (415) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_415_PAY_TO), "AI (415) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_415_PAY_TO).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (415) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_415_PAY_TO).getInterpretations().isEmpty(),
                "AI (415) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (415) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_415_PAY_TO);
        assertFalse(resp.isValid(),
                "Parsing AI (415) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (415) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_415_PAY_TO + "950600013435");
        assertFalse(resp.isValid(),
                "Parsing AI (415) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (415) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000123455");
        assertTrue(resp.isValid(), "A maximum-length value (13 chars) must be valid");
        assertEquals("9506000123455", resp.getAiObject().get(GS1Constants_AICodes.AI_415_PAY_TO).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (415) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "95060001234551");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (14 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (415) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "#506000134352");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (415) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134353");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (415) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_415_PAY_TO + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (415) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (415) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (415) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (415) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (415) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (415) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
