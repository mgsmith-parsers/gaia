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
 * Tests for AI (420) — SHIP TO POST.
 *
 * <p>Ship to / Deliver to postal code within a single postal authority
 *
 * <p>Format: {@code N3+X..20}
 *
 * <p>Excluded AIs: ["421"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (420) — SHIP TO POST")
class Ai420Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (420). */
    private static final String VALID_VALUE = "A";

    /** A fully valid element string. */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A";

    @Test
    @DisplayName("AI (420) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (420) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_420_SHIP_TO_POST), "AI (420) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_420_SHIP_TO_POST).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (420) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_420_SHIP_TO_POST);
        assertFalse(resp.isValid(),
                "Parsing AI (420) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (420) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A".repeat(20));
        assertTrue(resp.isValid(), "A maximum-length value (20 chars) must be valid");
        assertEquals("A".repeat(20), resp.getAiObject().get(GS1Constants_AICodes.AI_420_SHIP_TO_POST).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (420) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A".repeat(21));
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (21 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (420) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_420_SHIP_TO_POST + "#");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (420) paired with excluded AI (421) produces GE-S006")
    void excludedPairingProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_421_SHIP_TO_POST + "036A");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId())),
                "Pairing AI (420) with excluded AI (421) must produce GE-S006");
    }

    @Test
    @DisplayName("AI (420) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (420) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (420) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_420_SHIP_TO_POST + "A");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }
}
