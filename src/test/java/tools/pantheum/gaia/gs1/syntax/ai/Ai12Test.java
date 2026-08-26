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
 * Tests for AI (12) — DUE DATE.
 *
 * <p>Due date (YYMMDD)
 *
 * <p>Format: {@code N2+N6}
 *
 * <p>Required AIs: [["8020", "415"]]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (12) — DUE DATE")
class Ai12Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (12). */
    private static final String VALID_VALUE = "261200";

    /** A fully valid element string, including the required AIs (8020)+(415). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_8020_REF_NO + "A"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
            + GS1Constants_AICodes.AI_12_DUE_DATE + "261200";

    @Test
    @DisplayName("AI (12) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (12) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_12_DUE_DATE), "AI (12) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_12_DUE_DATE).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (12) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_12_DUE_DATE).getInterpretations().isEmpty(),
                "AI (12) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (12) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_12_DUE_DATE);
        assertFalse(resp.isValid(),
                "Parsing AI (12) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (12) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_12_DUE_DATE + "26120");
        assertFalse(resp.isValid(),
                "Parsing AI (12) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (12) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_12_DUE_DATE + "2612001");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (7 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (12) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_12_DUE_DATE + "#61200");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (12) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_12_DUE_DATE + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (12) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (12) followed by FNC1 produces a syntax error (fixed-length AI)")
    void trailingFnc1AfterFixedLengthAiProducesError() {
        // AI (12) is a fixed-length (predefined) AI — an FNC1 directly after it is a syntax error.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_8020_REF_NO + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_415_PAY_TO + "9506000134352"
                + GS1Constants_AICodes.AI_12_DUE_DATE + "261200"
                + GS1Constants.FNC1_GS);
        assertFalse(resp.isValid(),
                "FNC1 after fixed-length AI (12) must produce a syntax error (GE-S002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (12) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (12) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_12_DUE_DATE + "261200");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
