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
 * Tests for AI (8026) — ITIP CONTENT.
 *
 * <p>Identification of pieces of a trade item (ITIP) contained in a logistic unit
 *
 * <p>Format: {@code N4+N14+N2+N2}
 *
 * <p>Required AIs: [["00", "37"]]
 *
 * <p>Excluded AIs: ["02", "8006"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (8026) — ITIP CONTENT")
class Ai8026Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (8026). */
    private static final String VALID_VALUE = "095060001343520101";

    /** A fully valid element string, including the required AIs (00)+(37). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
            + GS1Constants_AICodes.AI_37_COUNT + "1"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "095060001343520101";

    @Test
    @DisplayName("AI (8026) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (8026) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_8026_ITIP_CONTENT), "AI (8026) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_8026_ITIP_CONTENT).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (8026) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_8026_ITIP_CONTENT).getInterpretations().isEmpty(),
                "AI (8026) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (8026) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8026_ITIP_CONTENT);
        assertFalse(resp.isValid(),
                "Parsing AI (8026) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8026) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "09506000134352010");
        assertFalse(resp.isValid(),
                "Parsing AI (8026) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8026) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "950600012345671111");
        assertTrue(resp.isValid(), "A maximum-length value (18 chars) must be valid");
        assertEquals("950600012345671111", resp.getAiObject().get(GS1Constants_AICodes.AI_8026_ITIP_CONTENT).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (8026) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "9506000123456711111");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (19 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (8026) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "#95060001343520101");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (8026) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "095060001343530101");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (8026) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8026_ITIP_CONTENT + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (8026) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (8026) paired with excluded AI (02) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "095060001343520101"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (8026) with excluded AI (02) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (8026) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "095060001343520101"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (8026) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (8026) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8026_ITIP_CONTENT + "095060001343520101");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
