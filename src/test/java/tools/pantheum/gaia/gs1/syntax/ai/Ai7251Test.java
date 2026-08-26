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
 * Tests for AI (7251) — DOB TIME.
 *
 * <p>Date and time of birth (YYYYMMDDhhmm)
 *
 * <p>Format: {@code N4+N12}
 *
 * <p>Required AIs: ["8018"]
 *
 * <p>Excluded AIs: ["7250"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (7251) — DOB TIME")
class Ai7251Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (7251). */
    private static final String VALID_VALUE = "202612311230";

    /** A fully valid element string, including the required AIs (8018). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_8018_GSRN_RECIPIENT + "950600012345678907"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_7251_DOB_TIME + "202612311230";

    @Test
    @DisplayName("AI (7251) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (7251) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_7251_DOB_TIME), "AI (7251) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_7251_DOB_TIME).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (7251) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_7251_DOB_TIME).getInterpretations().isEmpty(),
                "AI (7251) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (7251) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_7251_DOB_TIME);
        assertFalse(resp.isValid(),
                "Parsing AI (7251) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (7251) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_7251_DOB_TIME + "20261231123");
        assertFalse(resp.isValid(),
                "Parsing AI (7251) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (7251) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8018_GSRN_RECIPIENT + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7251_DOB_TIME + "2026123112301");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (13 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (7251) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8018_GSRN_RECIPIENT + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7251_DOB_TIME + "#02612311230");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (7251) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_7251_DOB_TIME + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (7251) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (7251) paired with excluded AI (7250) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8018_GSRN_RECIPIENT + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7251_DOB_TIME + "202612311230"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7250_DOB + "20261231");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (7251) with excluded AI (7250) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (7251) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8018_GSRN_RECIPIENT + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7251_DOB_TIME + "202612311230"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (7251) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (7251) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7251_DOB_TIME + "202612311230");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
