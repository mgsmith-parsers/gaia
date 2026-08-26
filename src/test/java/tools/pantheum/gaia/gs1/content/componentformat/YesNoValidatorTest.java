package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link YesNoValidator} — component format {@code yesno}. */
@DisplayName("YesNoValidator (yesno)")
class YesNoValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("yesno");

    @Test
    @DisplayName("is registered for format 'yesno'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'yesno'");
        assertTrue(validator instanceof YesNoValidator, "format 'yesno' must map to YesNoValidator");
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
        assertFalse(validator.validate("2", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "2 must be rejected with a reason");
    }
}
