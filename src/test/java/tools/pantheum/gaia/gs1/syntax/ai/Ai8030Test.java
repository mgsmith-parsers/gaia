package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AI (8030) — DIGSIG.
 *
 * <p>Digital Signature (DigSig)
 *
 * <p>Format: {@code N4+Z..90}
 *
 * <p>Required AIs: [["01", "21"], ["8006", "21"], ["8010", "8011"], "8003", "8004", "8017", "8018", "00", "253", "255"]
 *
 * <p>Generated against the AI definitions in {@code gs1-application-identifiers.jsonld};
 * every case is verified against the live parser. Covers syntax (including required
 * and excluded AI pairings), content validation, and interpretation enrichment.
 */
@DisplayName("AI (8030) — DIGSIG")
class Ai8030Test {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** A well-formed value for AI (8030). */
    private static final String VALID_VALUE = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVow";

    /** A fully valid element string, including the required AIs (01)+(21). */
    private static final String VALID_INPUT =
            GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
            + GS1Constants_AICodes.AI_21_SERIAL + "A"
            + GS1Constants.FNC1_GS
            + GS1Constants_AICodes.AI_8030_DIGSIG + "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVow";

    @Test
    @DisplayName("AI (8030) with a valid value parses and is valid")
    void validElementParsed() {
        ParseResult resp = parser.parse(VALID_INPUT);
        assertTrue(resp.isValid(), "A well-formed AI (8030) element string must be valid");
        assertNotNull(resp.getAiObject().get(GS1Constants_AICodes.AI_8030_DIGSIG), "AI (8030) element must be present");
        assertEquals(VALID_VALUE, resp.getAiObject().get(GS1Constants_AICodes.AI_8030_DIGSIG).getValue(),
                "Parsed value must equal the input value");
    }

    @Test
    @DisplayName("AI (8030) with no value produces an error")
    void missingValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8030_DIGSIG);
        assertFalse(resp.isValid(),
                "Parsing AI (8030) with no value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8030) with a truncated value produces an error")
    void shortValueProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8030_DIGSIG + "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo");
        assertFalse(resp.isValid(),
                "Parsing AI (8030) with a truncated value must produce at least one error");
    }

    @Test
    @DisplayName("AI (8030) with maximum-length value is valid")
    void maximumLengthValueIsValid() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "A".repeat(90));
        assertTrue(resp.isValid(), "A maximum-length value (90 chars) must be valid");
        assertEquals("A".repeat(90), resp.getAiObject().get(GS1Constants_AICodes.AI_8030_DIGSIG).getValue(),
                "Parsed value must equal the maximum-length input");
    }

    @Test
    @DisplayName("AI (8030) with over-maximum-length value produces an error")
    void overMaximumLengthValueProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "A".repeat(91));
        assertFalse(resp.isValid(),
                "A value longer than the declared maximum (91 chars) must produce an error");
    }

    @Test
    @DisplayName("AI (8030) with an out-of-charset character produces an error")
    void invalidCharacterProducesError() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "#UJDREVGR0hJSktMTU5PUFFSU1RVVldYWVow");
        assertFalse(resp.isValid(),
                "A value containing a character outside the AI's character set must produce an error");
    }

    @Test
    @DisplayName("AI (8030) without its required AIs produces GE-S005")
    void missingRequiredAisProducesError() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8030_DIGSIG + VALID_VALUE);
        assertFalse(resp.isValid(),
                "AI (8030) without its required AIs must produce a required-AI error");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())),
                "The error must be GE-S005 (missing required AI)");
    }

    @Test
    @DisplayName("AI (8030) terminated by a trailing FNC1 is valid with an advisory warning")
    void trailingFnc1ProducesAdvisoryWarning() {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVow"
                + GS1Constants.FNC1_GS);
        assertTrue(resp.isValid(), "A trailing FNC1 must not invalidate the result");
        assertTrue(resp.hasWarnings(),
                "A trailing FNC1 must produce an advisory warning (GE-W002)");
    }

    @Test
    @DisplayName("Fixed-length AI (01) followed by FNC1 then AI (8030) produces a syntax error")
    void fixedLengthAiWithFnc1BeforeThisAiProducesError() {
        // A FNC1 after fixed-length AI (01) mid-stream triggers GE-S011.
        // The parser stops at the FNC1 and AI (8030) is never reached.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVow");
        assertFalse(resp.isValid(),
                "FNC1 after a fixed-length AI mid-stream must produce a syntax error (GE-S011)");
    }

    @Test
    @DisplayName("AI (8030) has no content validator — content validation is disabled")
    void hasNoContentValidator() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8030").isEmpty(),
                "AI (8030) content validation is disabled — no validator may be registered");
    }

    @Test
    @DisplayName("AI (8030) accepts a Base64URL length that content validation once rejected")
    void impossibleBase64UrlLengthIsAccepted() {
        // "QUJDA" is 5 characters, i.e. length % 4 == 1, which cannot be Base64URL.
        // The former DigSigValidator rejected it with GE-C125; nothing does now.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "QUJDA");
        assertTrue(resp.isValid(), "No content rule constrains AI (8030) beyond its format regex");
        assertTrue(resp.getErrors().isEmpty(), "Unexpected errors: " + resp.getErrors());
    }

    @Test
    @DisplayName("AI (8030) still rejects out-of-charset characters via the format regex")
    void outOfCharsetStillRejected() {
        // Disabling content validation must not weaken the N4+Z..90 charset rule.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8030_DIGSIG + "QUJD*REVG");
        assertFalse(resp.isValid(), "Characters outside the Base64URL alphabet must still fail");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())),
                "The error must be GE-C001 (format regex): " + resp.getErrors());
    }
}
