package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link HhmmValidator} — component format {@code hhmm}. */
@DisplayName("HhmmValidator (hhmm)")
class HhmmValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("hhmm");

    @Test
    @DisplayName("is registered for format 'hhmm'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'hhmm'");
        assertTrue(validator instanceof HhmmValidator, "format 'hhmm' must map to HhmmValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("1230", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "1230 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("2460", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "2460 must be rejected with a reason");
    }
}
