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
 * Tests for AI (8008) — PROD TIME.
 *
 * <p>Date and time of production (YYMMDDhh[mm[ss]])
 *
 * <p>Format: {@code N4+N8[+N..4]}
 *
 * <p>Required AIs: ["01", "02"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (8008) — PROD TIME")
class Ai8008Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (8008). */
    private static final String VALID_VALUE = "26123123";

    /** A fully valid element string, including the required AIs (01). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
            + GS1Constants_AICodes.AI_8008_PROD_TIME + "26123123";

    @Test
    @DisplayName("AI (8008) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (8008) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_8008_PROD_TIME), "AI (8008) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_8008_PROD_TIME).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (8008) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_8008_PROD_TIME).getInterpretations().isEmpty(),
                "AI (8008) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (8008) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8008_PROD_TIME);
        assertFalse(resp.isValid(),
                "Parsing AI (8008) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8008) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8008_PROD_TIME + "2612312");
        assertFalse(resp.isValid(),
                "Parsing AI (8008) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8008) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "261231231");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (9 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (8008) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "#6123123");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (8008) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8008_PROD_TIME + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (8008) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (8008) with the optional MM[SS] time component present is valid")
    void optionalComponentPresentIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "2612312330");
        assertTrue(resp.isValid(),
                "AI (8008) with the optional MM[SS] time component present must be valid");
        assertEquals("2612312330", resp.getAiObject().get(GS1Constants_AICodes.AI_8008_PROD_TIME).getValue(),
                "Parsed value must include the optional component");
    }

    @Test
    @DisplayName("AI (8008) with an out-of-range minute (99) in the optional time produces GE-C001")
    void invalidOptionalComponentProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "2612312399");
        assertFalse(resp.isValid(),
                "AI (8008) with an out-of-range minute (99) in the optional time must produce a content error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())),
                "The error must be GE-C001");
    }

    @Test
    @DisplayName("AI (8008) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "26123123"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (8008) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (8008) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8008_PROD_TIME + "26123123");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
