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
 * Tests for AI (253) — GDTI.
 *
 * <p>Global Document Type Identifier (GDTI)
 *
 * <p>Format: {@code N3+N13[+X..17]}
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (253) — GDTI")
class Ai253Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (253). */
    private static final String VALID_VALUE = "9506000134352";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_253_GDTI + "9506000134352";

    @Test
    @DisplayName("AI (253) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (253) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_253_GDTI), "AI (253) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_253_GDTI).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (253) produces interpretations (INTERPRETATION mode)")
    void interpretationsPresent() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "Input must be valid for interpretations to be produced");
        assertFalse(resp.getAiObject().get(GS1Constants_AICodes.AI_253_GDTI).getInterpretations().isEmpty(),
                "AI (253) must produce at least one interpretation in INTERPRETATION mode");
    }

    @Test
    @DisplayName("AI (253) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_253_GDTI);
        assertFalse(resp.isValid(),
                "Parsing AI (253) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (253) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_253_GDTI + "950600013435");
        assertFalse(resp.isValid(),
                "Parsing AI (253) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (253) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000123455AAAAAAAAAAAAAAAAA");
        assertTrue(resp.isValid(), "A maximum-length value (30 chars) must be valid");
        assertEquals("9506000123455AAAAAAAAAAAAAAAAA", resp.getAiObject().get(GS1Constants_AICodes.AI_253_GDTI).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (253) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000123455AAAAAAAAAAAAAAAAAA");
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (31 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (253) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "#506000134352");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (253) with an incorrect check digit produces an error")
    void invalidCheckDigitProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000134353");
        assertFalse(resp.isValid(),
                "An incorrect check digit must produce an error (GE-C003)");
    }

    @Test
    @DisplayName("AI (253) with the optional serial component present is valid")
    void optionalComponentPresentIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000134352SER1");
        assertTrue(resp.isValid(),
                "AI (253) with the optional serial component present must be valid");
        assertEquals("9506000134352SER1", resp.getAiObject().get(GS1Constants_AICodes.AI_253_GDTI).getValue(),
                "Parsed value must include the optional component");
    }

    @Test
    @DisplayName("AI (253) with a serial containing an out-of-charset character produces GE-C001")
    void invalidOptionalComponentProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000134352#SER");
        assertFalse(resp.isValid(),
                "AI (253) with a serial containing an out-of-charset character must produce a content error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())),
                "The error must be GE-C001");
    }

    @Test
    @DisplayName("AI (253) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_253_GDTI + "9506000134352"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (253) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (253) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_253_GDTI + "9506000134352");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
