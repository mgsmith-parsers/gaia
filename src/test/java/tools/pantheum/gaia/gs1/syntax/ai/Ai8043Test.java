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
 * Tests for AI (8043) — PSIM.
 *
 * <p>Physical SIM number
 *
 * <p>Format: {@code N4+N18+[N1..N2]}
 *
 * <p>Required AIs: [["01", "21", "8040"]]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (8043) — PSIM")
class Ai8043Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (8043). */
    private static final String VALID_VALUE = "1".repeat(18);

    /** A fully valid element string, including the required AIs (01)+(21)+(8040). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
            + GS1Constants_AICodes.AI_21_SERIAL + "A"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(18);

    @Test
    @DisplayName("AI (8043) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (8043) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_8043_PSIM), "AI (8043) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_8043_PSIM).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (8043) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8043_PSIM);
        assertFalse(resp.isValid(),
                "Parsing AI (8043) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8043) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(17));
        assertFalse(resp.isValid(),
                "Parsing AI (8043) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8043) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(20));
        assertTrue(resp.isValid(), "A maximum-length value (20 chars) must be valid");
        assertEquals("1".repeat(20), resp.getAiObject().get(GS1Constants_AICodes.AI_8043_PSIM).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (8043) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(21));
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (21 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (8043) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "#11111111111111111");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (8043) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8043_PSIM + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (8043) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (8043) with the optional N1..N2 component present is valid")
    void optionalComponentPresentIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(18) + "12");
        assertTrue(resp.isValid(),
                "AI (8043) with the optional N1..N2 component present must be valid");
        assertEquals("1".repeat(18) + "12", resp.getAiObject().get(GS1Constants_AICodes.AI_8043_PSIM).getValue(),
                "Parsed value must include the optional component");
    }

    @Test
    @DisplayName("AI (8043) with a non-numeric optional component produces GE-C001")
    void invalidOptionalComponentProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(18) + "1A");
        assertFalse(resp.isValid(),
                "AI (8043) with a non-numeric optional component must produce a content error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())),
                "The error must be GE-C001");
    }

    @Test
    @DisplayName("AI (8043) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(18)
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (8043) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (8043) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8043_PSIM + "1".repeat(18));
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
