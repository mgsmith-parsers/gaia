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
 * Tests for AI (01) — GTIN.
 *
 * <p>Global Trade Item Number (GTIN)
 *
 * <p>Format: {@code N2+N14}
 *
 * <p>Excluded AIs: ["02", "03", "255", "37"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (01) — GTIN")
class Ai01Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (01). */
    private static final String VALID_VALUE = "09506000134352";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_01_GTIN + "09506000134352";

    @Test
    @DisplayName("AI (01) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (01) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_01_GTIN), "AI (01) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_01_GTIN).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (01) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_01_GTIN).getInterpretations().isEmpty(),
                "AI (01) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (01) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN);
        assertFalse(resp.isValid(),
                "Parsing AI (01) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (01) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "0950600013435");
        assertFalse(resp.isValid(),
                "Parsing AI (01) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (01) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "95060001234567");
        assertTrue(resp.isValid(), "A maximum-length value (14 chars) must be valid");
        assertEquals("95060001234567", resp.getAiObject().get(GS1Constants_AICodes.AI_01_GTIN).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (01) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "950600012345671");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (15 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (01) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "#9506000134352");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (01) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134353");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (01) paired with excluded AI (02) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_02_CONTENT + "09506000134352");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (01) with excluded AI (02) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (01) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (01) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (01) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (01) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (01) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
