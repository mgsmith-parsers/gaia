package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link Iso3166Data} — ISO 3166 country code dataset. */
@DisplayName("Iso3166Data")
class Iso3166DataTest {

    @Test
    @DisplayName("numeric code 036 resolves to Australia")
    void numericLookup() {
        assertEquals("Australia", Iso3166Data.nameForNumeric("036").orElseThrow());
    }

    @Test
    @DisplayName("alpha-2 code AU resolves to Australia")
    void alpha2Lookup() {
        assertEquals("Australia", Iso3166Data.nameForAlpha2("AU").orElseThrow());
    }

    @Test
    @DisplayName("unknown codes resolve to empty")
    void unknownCodes() {
        assertTrue(Iso3166Data.nameForNumeric("999").isEmpty());
        assertTrue(Iso3166Data.nameForAlpha2("XX").isEmpty());
    }
}
