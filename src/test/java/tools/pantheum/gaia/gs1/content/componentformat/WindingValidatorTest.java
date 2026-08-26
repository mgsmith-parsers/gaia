package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link WindingValidator} — component format {@code winding}. */
@DisplayName("WindingValidator (winding)")
class WindingValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("winding");

    @Test
    @DisplayName("is registered for format 'winding'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'winding'");
        assertTrue(validator instanceof WindingValidator, "format 'winding' must map to WindingValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("0", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "0 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("2", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "2 must be rejected with a reason");
    }
}
