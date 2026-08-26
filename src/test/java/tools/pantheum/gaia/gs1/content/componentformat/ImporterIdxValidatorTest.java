package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link ImporterIdxValidator} — component format {@code importeridx}. */
@DisplayName("ImporterIdxValidator (importeridx)")
class ImporterIdxValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("importeridx");

    @Test
    @DisplayName("is registered for format 'importeridx'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'importeridx'");
        assertTrue(validator instanceof ImporterIdxValidator, "format 'importeridx' must map to ImporterIdxValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("A", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "A must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("!", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "! must be rejected with a reason");
    }
}
