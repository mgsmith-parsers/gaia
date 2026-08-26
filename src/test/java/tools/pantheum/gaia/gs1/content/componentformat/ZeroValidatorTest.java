package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link ZeroValidator} — component format {@code zero}. */
@DisplayName("ZeroValidator (zero)")
class ZeroValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("zero");

    @Test
    @DisplayName("is registered for format 'zero'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'zero'");
        assertTrue(validator instanceof ZeroValidator, "format 'zero' must map to ZeroValidator");
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
        assertFalse(validator.validate("1", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "1 must be rejected with a reason");
    }
}
