package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link Iso5218Validator} — component format {@code iso5218}. */
@DisplayName("Iso5218Validator (iso5218)")
class Iso5218ValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("iso5218");

    @Test
    @DisplayName("is registered for format 'iso5218'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'iso5218'");
        assertTrue(validator instanceof Iso5218Validator, "format 'iso5218' must map to Iso5218Validator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("1", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "1 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("3", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "3 must be rejected with a reason");
    }
}
