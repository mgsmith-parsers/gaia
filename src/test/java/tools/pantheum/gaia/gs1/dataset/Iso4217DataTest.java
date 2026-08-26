package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link Iso4217Data} — ISO 4217 currency dataset. */
@DisplayName("Iso4217Data")
class Iso4217DataTest {

    @Test
    @DisplayName("numeric code 036 resolves to AUD")
    void numericLookup() {
        CurrencyEntry aud = Iso4217Data.forNumeric("036").orElseThrow();
        assertEquals("AUD", aud.getCode());
    }

    @Test
    @DisplayName("alpha code USD resolves with numeric 840")
    void alphaLookup() {
        CurrencyEntry usd = Iso4217Data.forAlpha("USD").orElseThrow();
        assertEquals("840", usd.getNumeric());
    }

    @Test
    @DisplayName("unknown codes resolve to empty")
    void unknownCodes() {
        assertTrue(Iso4217Data.forNumeric("999").isEmpty());
        assertTrue(Iso4217Data.forAlpha("XXX_NOT_A_CODE").isEmpty());
    }
}
