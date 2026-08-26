package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link Iso3166Validator} — component format {@code iso3166}. */
@DisplayName("Iso3166Validator (iso3166)")
class Iso3166ValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("iso3166");

    @Test
    @DisplayName("is registered for format 'iso3166'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'iso3166'");
        assertTrue(validator instanceof Iso3166Validator, "format 'iso3166' must map to Iso3166Validator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("036", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "036 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("999", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "999 must be rejected with a reason");
    }
}
