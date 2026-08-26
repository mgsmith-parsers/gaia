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
 * Tests for AI (3938) — PRICE.
 *
 * <p>Applicable amount payable with ISO currency code (variable measure trade item)
 *
 * <p>Format: {@code N4+N3+N..15}
 *
 * <p>Required AIs: ["30", {"start": "3100", "end": "3195"}, {"start": "3200", "end": "3295"}, {"start": "3500", "end": "3595"}, {"start": "3600", "end": "3695"}]
 *
 * <p>Excluded AIs: [{"start": "3930", "end": "3939"}]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (3938) — PRICE")
class Ai3938Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (3938). */
    private static final String VALID_VALUE = "0361";

    /** A fully valid element string, including the required AIs (30)+(01). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
            + GS1Constants_AICodes.AI_3938_PRICE + "0361";

    @Test
    @DisplayName("AI (3938) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (3938) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_3938_PRICE), "AI (3938) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_3938_PRICE).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (3938) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_3938_PRICE).getInterpretations().isEmpty(),
                "AI (3938) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (3938) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3938_PRICE);
        assertFalse(resp.isValid(),
                "Parsing AI (3938) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3938) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3938_PRICE + "036");
        assertFalse(resp.isValid(),
                "Parsing AI (3938) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3938) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3938_PRICE + "036111111111111111");
        assertTrue(resp.isValid(), "A maximum-length value (18 chars) must be valid");
        assertEquals("036111111111111111", resp.getAiObject().get(GS1Constants_AICodes.AI_3938_PRICE).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (3938) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3938_PRICE + "0361111111111111111");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (19 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (3938) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3938_PRICE + "#361");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (3938) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3938_PRICE + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (3938) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (3938) paired with excluded AI (3930) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3938_PRICE + "0361"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_3930_PRICE + "0361");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (3938) with excluded AI (3930) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (3938) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_30_VAR_COUNT + "1"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_3938_PRICE + "0361"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (3938) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (3938) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_3938_PRICE + "0361");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
