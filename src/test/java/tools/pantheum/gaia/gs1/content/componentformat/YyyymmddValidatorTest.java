package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link YyyymmddValidator} — component format {@code yyyymmdd}. */
@DisplayName("YyyymmddValidator (yyyymmdd)")
class YyyymmddValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("yyyymmdd");

    @Test
    @DisplayName("is registered for format 'yyyymmdd'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'yyyymmdd'");
        assertTrue(validator instanceof YyyymmddValidator, "format 'yyyymmdd' must map to YyyymmddValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("20261231", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "20261231 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("20261301", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "20261301 must be rejected with a reason");
    }
}
