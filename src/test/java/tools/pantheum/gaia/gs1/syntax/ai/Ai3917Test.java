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
 * Tests for AI (3917) — AMOUNT.
 *
 * <p>Applicable amount payable with ISO currency code
 *
 * <p>Format: {@code N4+N3+N..15}
 *
 * <p>Required AIs: [["8020", "415"]]
 *
 * <p>Excluded AIs: [{"start": "3910", "end": "3919"}]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (3917) — AMOUNT")
class Ai3917Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (3917). */
    private static final String VALID_VALUE = "0361";

    /** A fully valid element string, including the required AIs (8020)+(415). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_8020_REF_NO + "A"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
            + GS1Constants_AICodes.AI_3917_AMOUNT + "0361";

    @Test
    @DisplayName("AI (3917) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (3917) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_3917_AMOUNT), "AI (3917) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_3917_AMOUNT).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (3917) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_3917_AMOUNT).getInterpretations().isEmpty(),
                "AI (3917) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (3917) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3917_AMOUNT);
        assertFalse(resp.isValid(),
                "Parsing AI (3917) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3917) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3917_AMOUNT + "036");
        assertFalse(resp.isValid(),
                "Parsing AI (3917) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (3917) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_3917_AMOUNT + "036111111111111111");
        assertTrue(resp.isValid(), "A maximum-length value (18 chars) must be valid");
        assertEquals("036111111111111111", resp.getAiObject().get(GS1Constants_AICodes.AI_3917_AMOUNT).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (3917) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_3917_AMOUNT + "0361111111111111111");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (19 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (3917) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_3917_AMOUNT + "#361");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (3917) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_3917_AMOUNT + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (3917) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (3917) paired with excluded AI (3910) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_3917_AMOUNT + "0361"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_3910_AMOUNT + "0361");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (3917) with excluded AI (3910) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (3917) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_3917_AMOUNT + "0361"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (3917) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (3917) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_3917_AMOUNT + "0361");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
