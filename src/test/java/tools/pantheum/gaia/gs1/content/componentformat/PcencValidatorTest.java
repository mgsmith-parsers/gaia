package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link PcencValidator} — component format {@code pcenc}. */
@DisplayName("PcencValidator (pcenc)")
class PcencValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("pcenc");

    @Test
    @DisplayName("is registered for format 'pcenc'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'pcenc'");
        assertTrue(validator instanceof PcencValidator, "format 'pcenc' must map to PcencValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("ABC", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "ABC must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("%2", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "%2 must be rejected with a reason");
    }
}
