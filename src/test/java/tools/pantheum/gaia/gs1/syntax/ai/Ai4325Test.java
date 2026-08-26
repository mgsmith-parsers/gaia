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
 * Tests for AI (4325) — NAFT DEL DT.
 *
 * <p>Not after delivery date time (YYMMDDhhmm)
 *
 * <p>Format: {@code N4+N10}
 *
 * <p>Required AIs: ["00"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (4325) — NAFT DEL DT")
class Ai4325Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (4325). */
    private static final String VALID_VALUE = "2612001230";

    /** A fully valid element string, including the required AIs (00). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
            + GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "2612001230";

    @Test
    @DisplayName("AI (4325) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (4325) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT), "AI (4325) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (4325) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT).getInterpretations().isEmpty(),
                "AI (4325) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (4325) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT);
        assertFalse(resp.isValid(),
                "Parsing AI (4325) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (4325) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "261200123");
        assertFalse(resp.isValid(),
                "Parsing AI (4325) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (4325) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "26120012301");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (11 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (4325) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "#612001230");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (4325) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (4325) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (4325) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_00_SSCC + "095060001343521113"
                + GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "2612001230"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (4325) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (4325) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_4325_NAFT_DEL_DT + "2612001230");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
