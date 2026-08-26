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
 * Tests for AI (3273) — HEIGHT (in).
 *
 * <p>Depth, thickness, height, or third dimension, inches (variable measure trade item)
 *
 * <p>Format: {@code N4+N6}
 *
 * <p>Required AIs: ["01", "02"]
 *
 * <p>Excluded AIs: [{"start": "3270", "end": "3275"}]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (3273) — HEIGHT (in)")
class Ai3273Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (3273). */
    private static final String VALID_VALUE = "111111";

    /** A fully valid element string, including the required AIs (01). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
            + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "111111";

    @Test
    @DisplayName("AI (3273) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (3273) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_3273_HEIGHT_IN), "AI (3273) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_3273_HEIGHT_IN).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (3273) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_3273_HEIGHT_IN).getInterpretations().isEmpty(),
                "AI (3273) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (3273) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3273_HEIGHT_IN);
        assertFalse(resp.isValid(),
                "Parsing AI (3273) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3273) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3273_HEIGHT_IN + "11111");
        assertFalse(resp.isValid(),
                "Parsing AI (3273) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3273) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "1111111");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (7 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (3273) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "#11111");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (3273) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3273_HEIGHT_IN + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (3273) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (3273) paired with excluded AI (3270) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "111111"
                + GS1Constants_AICodes.AI_3270_HEIGHT_IN + "111111");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (3273) with excluded AI (3270) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (3273) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (3273) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "111111"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (3273) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (3273) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (3273) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_3273_HEIGHT_IN + "111111");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
