package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ImeiValidator} — the IMEI Luhn check-digit validation on
 * AI 8040 (IMEI) and AI 8041 (IMEI2).
 *
 * <p>Both AIs require (01)+(21) (and 8041 additionally requires 8040), so each
 * input prepends the required AIs; otherwise the structural {@code GE-S005} check
 * halts the parse before the content stage where {@link ImeiValidator} runs.
 *
 * <h3>IMEI values used</h3>
 * <pre>
 *   490154203237518  Luhn-valid   (TAC 49015420, serial 323751, check 8)
 *   490154203237519  Luhn-invalid (check digit changed 8 → 9)
 *   356938035643809  Luhn-valid   (used for 8041)
 *   356938035643800  Luhn-invalid (check digit changed 9 → 0)
 * </pre>
 */
@DisplayName("ImeiValidator — AI 8040 / 8041")
class ImeiValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** Wraps an IMEI in a full element string with the required AIs (01)+(21). */
    private static String with8040(String imei) {
        return GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + imei;
    }

    @Test
    @DisplayName("Luhn-valid IMEI passes")
    void validImei() {
        ParseResult resp = parser.parse(with8040("490154203237518"));
        assertTrue(resp.isValid(), () -> "Expected valid but got: " + resp.getErrors());
    }

    @Test
    @DisplayName("IMEI with a bad Luhn check digit is rejected with GE-C169")
    void invalidLuhn() {
        ParseResult resp = parser.parse(with8040("490154203237519"));
        assertFalse(resp.isValid(), "A Luhn-invalid IMEI must fail content validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C169".equals(e.getId())),
                "The error must be GE-C169 (IMEI check digit)");
    }

    @Test
    @DisplayName("the same validator governs AI 8041")
    void appliesToImei2() {
        // 8041 requires (01)+(21)+(8040): supply a valid 8040, then a Luhn-invalid 8041.
        String input = with8040("490154203237518")
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8041_IMEI2 + "356938035643800";
        ParseResult resp = parser.parse(input);
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C169".equals(e.getId())),
                "AI (8041) must use the same IMEI Luhn check (GE-C169)");
    }
}
