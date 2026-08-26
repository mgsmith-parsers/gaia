package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link NonzeroValidator} — component format {@code nonzero}. */
@DisplayName("NonzeroValidator (nonzero)")
class NonzeroValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("nonzero");

    @Test
    @DisplayName("is registered for format 'nonzero'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'nonzero'");
        assertTrue(validator instanceof NonzeroValidator, "format 'nonzero' must map to NonzeroValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("01", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "01 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("00", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "00 must be rejected with a reason");
    }
}
