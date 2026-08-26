package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link Iso3166ListValidator} — component format {@code iso3166list}. */
@DisplayName("Iso3166ListValidator (iso3166list)")
class Iso3166ListValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("iso3166list");

    @Test
    @DisplayName("is registered for format 'iso3166list'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'iso3166list'");
        assertTrue(validator instanceof Iso3166ListValidator, "format 'iso3166list' must map to Iso3166ListValidator");
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
