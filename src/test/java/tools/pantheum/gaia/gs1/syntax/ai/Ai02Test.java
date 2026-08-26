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
 * Tests for AI (02) — CONTENT.
 *
 * <p>Global Trade Item Number (GTIN) of contained trade items
 *
 * <p>Format: {@code N2+N14}
 *
 * <p>Required AIs: [["00", "37"]]
 *
 * <p>Excluded AIs: ["01", "03"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (02) — CONTENT")
class Ai02Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (02). */
    private static final String VALID_VALUE = "09506000134352";

    /** A fully valid element string, including the required AIs (00)+(37). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
            + GS1Constants_AICodes.AI_37_COUNT + "1"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352";

    @Test
    @DisplayName("AI (02) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (02) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_02_CONTENT), "AI (02) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_02_CONTENT).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (02) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_02_CONTENT).getInterpretations().isEmpty(),
                "AI (02) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (02) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_02_CONTENT);
        assertFalse(resp.isValid(),
                "Parsing AI (02) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (02) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_02_CONTENT + "0950600013435");
        assertFalse(resp.isValid(),
                "Parsing AI (02) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (02) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "95060001234567");
        assertTrue(resp.isValid(), "A maximum-length value (14 chars) must be valid");
        assertEquals("95060001234567", resp.getAiObject().get(GS1Constants_AICodes.AI_02_CONTENT).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (02) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "950600012345671");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (15 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (02) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "#9506000134352");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (02) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134353");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (02) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_02_CONTENT + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (02) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (02) paired with excluded AI (01) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352"
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (02) with excluded AI (01) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (02) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (02) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_37_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (02) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (02) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (02) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
