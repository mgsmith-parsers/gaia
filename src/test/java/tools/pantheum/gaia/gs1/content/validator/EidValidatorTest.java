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
 * Tests for {@link EidValidator} — the EID Luhn check-digit validation on
 * AI 8042 (ESIM).
 *
 * <p>AI 8042 requires (01)+(21)+(8040), so each input prepends those; otherwise the
 * structural {@code GE-S005} check halts the parse before the content stage where
 * {@link EidValidator} runs.
 *
 * <h3>EID values used</h3>
 * <pre>
 *   89044030050088826003380898765430  Luhn-valid   (MII 89, check digit 0)
 *   89044030050088826003380898765431  Luhn-invalid (check digit changed 0 → 1)
 * </pre>
 */
@DisplayName("EidValidator — AI 8042 (ESIM)")
class EidValidatorTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** Wraps an EID in a full element string with the required AIs (01)+(21)+(8040). */
    private static String with8042(String eid) {
        return GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8042_ESIM + eid;
    }

    @Test
    @DisplayName("Luhn-valid EID passes")
    void validEid() {
        ParseResult resp = parser.parse(with8042("89044030050088826003380898765430"));
        assertTrue(resp.isValid(), () -> "Expected valid but got: " + resp.getErrors());
    }

    @Test
    @DisplayName("EID with a bad Luhn check digit is rejected with GE-C170")
    void invalidLuhn() {
        ParseResult resp = parser.parse(with8042("89044030050088826003380898765431"));
        assertFalse(resp.isValid(), "A Luhn-invalid EID must fail content validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C170".equals(e.getId())),
                "The error must be GE-C170 (EID check digit)");
    }
}
