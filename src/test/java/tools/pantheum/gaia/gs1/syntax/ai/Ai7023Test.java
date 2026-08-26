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
 * Tests for AI (7023) — GIAI - ASSEMBLY.
 *
 * <p>Global Individual Asset Identifier (GIAI) of an assembly
 *
 * <p>Format: {@code N4+X..30}
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (7023) — GIAI - ASSEMBLY")
class Ai7023Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (7023). */
    private static final String VALID_VALUE = "9506000123";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY + "9506000123";

    @Test
    @DisplayName("AI (7023) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (7023) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY), "AI (7023) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (7023) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY).getInterpretations().isEmpty(),
                "AI (7023) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (7023) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY);
        assertFalse(resp.isValid(),
                "Parsing AI (7023) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (7023) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY + "A".repeat(31));
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (31 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (7023) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY + "#506000123");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (7023) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY + "9506000123"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (7023) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (7023) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7023_GIAI_ASSEMBLY + "9506000123");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
