package tools.pantheum.gaia;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.modifier.TestModifiers;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GaiaParserTest {

    static GaiaParser parser;
    private static final char gs = GS1Constants.FNC1_GS;

    @BeforeAll
    static void setup() {
        parser = new GaiaParser();
    }

    // -------------------------------------------------------------------------
    // Happy-path: single and concatenated element strings
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Single GTIN-14 with valid check digit")
    void singleGtin() {
        // GTIN 09506000134352 — check digit 2 is correct
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352");
        assertTrue(resp.isValid(), () -> "Expected no errors but got: " + resp.getErrors());
        assertEquals(1, resp.getAiObject().getAis().size());
        GS1AIObjectElement e = resp.getAiObject().getAis().get(0);
        assertEquals(GS1Constants_AICodes.AI_01_GTIN, e.getAi());
        assertEquals("09506000134352", e.getValue());
    }

    @Test
    @DisplayName("GTIN + expiry date concatenated (fixed-length, no separator needed)")
    void gtinPlusExpiryDate() {
        // AI(01) + AI(17): both fixed-length, no GS separator required
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231");
        assertTrue(resp.isValid(), () -> "Errors: " + resp.getErrors());
        assertEquals(2, resp.getAiObject().getAis().size());
        assertEquals(GS1Constants_AICodes.AI_01_GTIN, resp.getAiObject().getAis().get(0).getAi());
        assertEquals(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY, resp.getAiObject().getAis().get(1).getAi());
        assertEquals("271231", resp.getAiObject().getAis().get(1).getValue());
    }

    @Test
    @DisplayName("Variable-length AI (batch/lot) with GS separator")
    void batchLotWithSeparator() {
        // AI(01) fixed, then AI(10) variable, separated by GS (0x1D)
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_10_BATCH_LOT + "LOT-ABC" + gs + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231";
        ParseResult resp = parser.parse(input);
        assertTrue(resp.isValid(), () -> "Errors: " + resp.getErrors());
        assertEquals(3, resp.getAiObject().getAis().size());
        assertEquals(GS1Constants_AICodes.AI_10_BATCH_LOT, resp.getAiObject().getAis().get(1).getAi());
        assertEquals("LOT-ABC", resp.getAiObject().getAis().get(1).getValue());
    }

    @Test
    @DisplayName("Unexpected FNC1 after fixed-length AI raises GE-S011 and stops parsing")
    void unexpectedFnc1AfterFixedLengthAi() {
        // AI(01) is fixed-length; a GS after it is unexpected.
        // GE-S011 (SYNTAX_ERROR) is raised and parsing stops immediately — AI(01) is
        // not added to the element list (break fires before elements.add).
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + gs + "10" + "BATCH99";
        ParseResult resp = parser.parse(input);
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream().anyMatch(e -> e.getId().equals("GE-S011")));
        assertEquals(0, resp.getAiObject().getAis().size());
    }

    @Test
    @DisplayName("SSCC (AI 00) with valid check digit")
    void sscc() {
        // SSCC: 18 digits, check digit is last
        // Using the example from spec section 7.9.1: 376104250021234569
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_00_SSCC + "376104250021234569");
        assertTrue(resp.isValid(), () -> "Errors: " + resp.getErrors());
        assertEquals("376104250021234569", resp.getAiObject().getAis().get(0).getValue());
    }

    // -------------------------------------------------------------------------
    // Syntax errors
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("SYNTAX_ERROR — unknown AI prefix")
    void unknownAi() {
        // "ZZ" is not a valid AI prefix
        ParseResult resp = parser.parse("ZZ123456");
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.SYNTAX_ERROR
                            && "UNKNOWN_AI".equals(e.getCode())));
    }

    @Test
    @DisplayName("SYNTAX_ERROR — truncated AI code (input too short)")
    void truncatedAi() {
        ParseResult resp = parser.parse("0");  // only one digit, AI needs at least 2
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.SYNTAX_ERROR
                            && e.getId().equals("GE-S002")));
    }

    @Test
    @DisplayName("SYNTAX_ERROR — truncated data field for fixed-length AI")
    void truncatedData() {
        // AI(01) needs 14 data digits; give only 6
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "123456");
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.SYNTAX_ERROR
                            && e.getId().equals("GE-S003")));
    }

    // -------------------------------------------------------------------------
    // Integrity errors
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("INTEGRITY_ERROR — duplicate AI")
    void duplicateAi() {
        // Two AI(17) in one element string
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231";
        ParseResult resp = parser.parse(input);
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.INTEGRITY_ERROR
                            && "DUPLICATE_AI".equals(e.getCode())));
    }

    @Test
    @DisplayName("INTEGRITY_ERROR — AI(10) present without required partner")
    void missingRequired() {
        // AI(10) requires one of 01,02,03,8006,8026 — provide neither
        String input = "10" + "LOT123"; // no GTIN
        ParseResult resp = parser.parse(input);
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.INTEGRITY_ERROR
                            && "MISSING_REQUIRED_AI".equals(e.getCode())));
    }

    // -------------------------------------------------------------------------
    // Content / format errors
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("FORMAT_ERROR — invalid date (month 13)")
    void invalidDateMonth() {
        // AI(17) expiry date with month = 13
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271301";
        ParseResult resp = parser.parse(input);
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.FORMAT_ERROR));
    }

    @Test
    @DisplayName("FORMAT_ERROR — invalid date (day 00 is valid, day 32 is not)")
    void invalidDateDay() {
        // Day 00 is valid (last day of month); day 32 is not
        String validInput   = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271200"; // day=00 ok
        String invalidInput = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271232"; // day=32

        ParseResult validResp = parser.parse(validInput);
        // May still have other errors (e.g. check digit on GTIN); only assert no date error.
        long dateErrors = validResp.getErrors().stream()
                .filter(e -> "GE-C001".equals(e.getId()) || "GE-C025".equals(e.getId())).count();
        assertEquals(0, dateErrors, "Day 00 should be valid");

        ParseResult invalidResp = parser.parse(invalidInput);
        // Day 32 may be caught by the regex (GE-C001) or the format validator (GE-C025, yymmd0);
        // in both cases it is a FORMAT_ERROR.
        assertTrue(invalidResp.getErrors().stream()
                        .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.FORMAT_ERROR),
                "Day 32 should produce a format error");
    }

    @Test
    @DisplayName("DATA_ERROR — invalid GTIN check digit")
    void invalidCheckDigit() {
        // GTIN 09506000134350 — check digit should be 2, not 0
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134350");
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.DATA_ERROR
                            && "INVALID_CHECK_DIGIT".equals(e.getCode())));
    }

    @Test
    @DisplayName("DATA_ERROR — invalid SSCC check digit")
    void invalidSsccCheckDigit() {
        // Correct SSCC is 376104250021234569; change last digit
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_00_SSCC + "376104250021234561");
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream()
                .anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.DATA_ERROR
                            && "INVALID_CHECK_DIGIT".equals(e.getCode())));
    }

    // -------------------------------------------------------------------------
    // Null / empty input
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Empty input returns empty response with no errors")
    void emptyInput() {
        ParseResult resp = parser.parse("");
        assertTrue(resp.isValid());
        assertTrue(resp.getAiObject().getAis().isEmpty());
    }

    @Test
    @DisplayName("Null input returns empty response with no errors")
    void nullInput() {
        ParseResult resp = parser.parse(null);
        assertTrue(resp.isValid());
        assertTrue(resp.getAiObject().getAis().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Input modifiers
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Modifier rewrites the input before parsing; result records the original")
    void modifierRewritesInput() {
        // {GS} placeholder stands in for the real separator — unparseable until the modifier runs
        String tail  = GS1Constants_AICodes.AI_10_BATCH_LOT + "LOT-ABC";
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + tail + "{GS}"
                + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231";
        ParseConfig config = ParseConfig.builder()
                .modifier(new TestModifiers.GsPlaceholder())
                .build();

        ParseResult resp = parser.parse(input, config);

        assertTrue(resp.isValid(), () -> "Errors: " + resp.getErrors());
        assertEquals(3, resp.getAiObject().getAis().size());
        assertEquals("LOT-ABC", resp.getAiObject().getAis().get(1).getValue());

        assertTrue(resp.isInputModified());
        assertEquals(input, resp.getModifierInfo().getOriginalInput());
        assertEquals(GS1Constants_AICodes.AI_01_GTIN + "09506000134352" + tail + gs
                + GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY + "271231", resp.getPayload());
        assertEquals(java.util.List.of("GsPlaceholder"), resp.getModifierInfo().getAppliedModifiers());
    }

    @Test
    @DisplayName("Modifiers run before correlation-ID stripping")
    void modifierRunsBeforeCorrelationStripping() {
        // The correlation prefix only becomes visible after the wrapper is stripped
        ParseConfig config = ParseConfig.builder()
                .modifier(new TestModifiers.StripScanPrefix())
                .build();

        ParseResult resp = parser.parse("SCAN:12345678~" + GS1Constants_AICodes.AI_01_GTIN + "09506000134352", config);

        assertTrue(resp.isValid(), () -> "Errors: " + resp.getErrors());
        assertTrue(resp.hasCorrelationId());
        assertEquals("12345678", resp.getCorrelationInfo().getId());
        assertTrue(resp.isInputModified());
    }

    @Test
    @DisplayName("No modifiers configured — no ModifierInfo on the result")
    void noModifierInfoWithoutModifiers() {
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352");

        assertNull(resp.getModifierInfo());
        assertFalse(resp.isInputModified());
    }

    @Test
    @DisplayName("Modifier that leaves the input alone reports not modified")
    void noOpModifier() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.NoOp()).build();

        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352", config);

        assertTrue(resp.isValid());
        assertNotNull(resp.getModifierInfo());
        assertFalse(resp.isInputModified());
    }

    @Test
    @DisplayName("A throwing modifier aborts the parse with a GE-I001 internal error")
    void throwingModifierAbortsParse() {
        String input = GS1Constants_AICodes.AI_01_GTIN + "09506000134352";
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.Throwing()).build();

        ParseResult resp = parser.parse(input, config);

        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-I001".equals(e.getId())),
                () -> "Expected GE-I001 but got: " + resp.getErrors());
        assertTrue(resp.getErrors().stream().anyMatch(e -> e.getMessage().contains("Throwing")),
                () -> "Expected the failing modifier to be named: " + resp.getErrors());
        assertEquals(input, resp.getPayload());   // the unmodified input is reported back
    }
}
