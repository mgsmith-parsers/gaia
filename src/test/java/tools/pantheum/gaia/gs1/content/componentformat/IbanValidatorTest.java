package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link IbanValidator} — component format {@code iban}. */
@DisplayName("IbanValidator (iban)")
class IbanValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("iban");

    @Test
    @DisplayName("is registered for format 'iban'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'iban'");
        assertTrue(validator instanceof IbanValidator, "format 'iban' must map to IbanValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("GB82WEST12345698765432", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "GB82WEST12345698765432 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("XX82WEST12345698765432", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "XX82WEST12345698765432 must be rejected with a reason");
    }
}
