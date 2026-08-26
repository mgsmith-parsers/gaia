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
 * Tests for AI (4332) — MIN TEMP F.
 *
 * <p>Minimum temperature in Fahrenheit (expressed in hundredths of degrees)
 *
 * <p>Format: {@code N4+N6+[-]}
 *
 * <p>Required AIs: ["00"]
 *
 * <p>Excluded AIs: ["4333"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (4332) — MIN TEMP F")
class Ai4332Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (4332). */
    private static final String VALID_VALUE = "111111";

    /** A fully valid element string, including the required AIs (00). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
            + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111";

    @Test
    @DisplayName("AI (4332) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (4332) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_4332_MIN_TEMP_F), "AI (4332) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_4332_MIN_TEMP_F).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (4332) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_4332_MIN_TEMP_F).getInterpretations().isEmpty(),
                "AI (4332) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (4332) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4332_MIN_TEMP_F);
        assertFalse(resp.isValid(),
                "Parsing AI (4332) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (4332) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "11111");
        assertFalse(resp.isValid(),
                "Parsing AI (4332) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (4332) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111AA");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (8 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (4332) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "#11111");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (4332) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4332_MIN_TEMP_F + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (4332) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (4332) paired with excluded AI (4333) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_4333_MIN_TEMP_C + "111111");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (4332) with excluded AI (4333) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (4332) with the optional minus (negative temperature) flag present is valid")
    void optionalComponentPresentIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111-");
        assertTrue(resp.isValid(),
                "AI (4332) with the optional minus (negative temperature) flag present must be valid");
        assertEquals("111111-", resp.getAiObject().get(GS1Constants_AICodes.AI_4332_MIN_TEMP_F).getValue(),
                "Parsed value must include the optional component");
    }

    @Test
    @DisplayName("AI (4332) with a character other than minus in the optional position produces GE-C001")
    void invalidOptionalComponentProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111+");
        assertFalse(resp.isValid(),
                "AI (4332) with a character other than minus in the optional position must produce a content error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())),
                "The error must be GE-C001");
    }

    @Test
    @DisplayName("AI (4332) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (4332) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (4332) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_4332_MIN_TEMP_F + "111111");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
