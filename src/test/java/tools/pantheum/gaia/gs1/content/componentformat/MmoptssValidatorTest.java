package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link MmoptssValidator} — component format {@code mmoptss}. */
@DisplayName("MmoptssValidator (mmoptss)")
class MmoptssValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("mmoptss");

    @Test
    @DisplayName("is registered for format 'mmoptss'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'mmoptss'");
        assertTrue(validator instanceof MmoptssValidator, "format 'mmoptss' must map to MmoptssValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("30", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "30 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("60", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "60 must be rejected with a reason");
    }
}
