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
 * Tests for AI (413) — SHIP FOR LOC.
 *
 * <p>Ship for / Deliver for - Forward to Global Location Number (GLN)
 *
 * <p>Format: {@code N3+N13}
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (413) — SHIP FOR LOC")
class Ai413Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (413). */
    private static final String VALID_VALUE = "9506000134352";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "9506000134352";

    @Test
    @DisplayName("AI (413) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (413) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC), "AI (413) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (413) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC).getInterpretations().isEmpty(),
                "AI (413) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (413) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC);
        assertFalse(resp.isValid(),
                "Parsing AI (413) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (413) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "950600013435");
        assertFalse(resp.isValid(),
                "Parsing AI (413) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (413) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "9506000123455");
        assertTrue(resp.isValid(), "A maximum-length value (13 chars) must be valid");
        assertEquals("9506000123455", resp.getAiObject().get(GS1Constants_AICodes.AI_413_SHIP_FOR_LOC).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (413) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "95060001234551");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (14 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (413) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "#506000134352");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (413) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "9506000134353");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (413) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (413) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "9506000134352"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (413) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (413) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (413) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_413_SHIP_FOR_LOC + "9506000134352");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
