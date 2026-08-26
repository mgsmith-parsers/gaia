package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link CurrencyEntry} — currency metadata accessors. */
@DisplayName("CurrencyEntry")
class CurrencyEntryTest {

    @Test
    @DisplayName("AUD entry carries complete formatting metadata")
    void audMetadata() {
        CurrencyEntry aud = Iso4217Data.forAlpha("AUD").orElseThrow();
        assertEquals("036", aud.getNumeric());
        assertEquals("AUD", aud.getCode());
        assertNotNull(aud.getName());
        assertNotNull(aud.getSymbol());
        assertTrue(aud.getDecimalPlaces() >= 0);
        assertNotNull(aud.getSymbolPosition());
    }
}
