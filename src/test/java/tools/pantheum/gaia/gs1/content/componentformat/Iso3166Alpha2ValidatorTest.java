package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link Iso3166Alpha2Validator} — component format {@code iso3166alpha2}. */
@DisplayName("Iso3166Alpha2Validator (iso3166alpha2)")
class Iso3166Alpha2ValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("iso3166alpha2");

    @Test
    @DisplayName("is registered for format 'iso3166alpha2'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'iso3166alpha2'");
        assertTrue(validator instanceof Iso3166Alpha2Validator, "format 'iso3166alpha2' must map to Iso3166Alpha2Validator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("AU", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "AU must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("XX", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "XX must be rejected with a reason");
    }
}
