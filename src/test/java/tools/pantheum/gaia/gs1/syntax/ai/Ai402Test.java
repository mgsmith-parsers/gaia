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
 * Tests for AI (402) — GSIN.
 *
 * <p>Global Shipment Identification Number (GSIN)
 *
 * <p>Format: {@code N3+N17}
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (402) — GSIN")
class Ai402Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (402). */
    private static final String VALID_VALUE = "95060001234567893";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_402_GSIN + "95060001234567893";

    @Test
    @DisplayName("AI (402) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (402) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_402_GSIN), "AI (402) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_402_GSIN).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (402) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_402_GSIN).getInterpretations().isEmpty(),
                "AI (402) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (402) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_402_GSIN);
        assertFalse(resp.isValid(),
                "Parsing AI (402) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (402) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_402_GSIN + "9506000123456789");
        assertFalse(resp.isValid(),
                "Parsing AI (402) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (402) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_402_GSIN + "950600012345678931");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (18 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (402) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_402_GSIN + "#5060001234567893");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (402) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_402_GSIN + "95060001234567894");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (402) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_402_GSIN + "95060001234567893"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (402) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (402) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_402_GSIN + "95060001234567893");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
