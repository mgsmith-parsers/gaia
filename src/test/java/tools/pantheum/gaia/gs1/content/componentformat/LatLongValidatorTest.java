package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link LatLongValidator} — component format {@code latlong}. */
@DisplayName("LatLongValidator (latlong)")
class LatLongValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("latlong");

    @Test
    @DisplayName("is registered for format 'latlong'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'latlong'");
        assertTrue(validator instanceof LatLongValidator, "format 'latlong' must map to LatLongValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("00000000000000000000", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "00000000000000000000 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("99999999999999999999", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "99999999999999999999 must be rejected with a reason");
    }
}
