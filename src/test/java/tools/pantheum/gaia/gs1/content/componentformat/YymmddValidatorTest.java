package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link YymmddValidator} — component format {@code yymmdd}. */
@DisplayName("YymmddValidator (yymmdd)")
class YymmddValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("yymmdd");

    @Test
    @DisplayName("is registered for format 'yymmdd'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'yymmdd'");
        assertTrue(validator instanceof YymmddValidator, "format 'yymmdd' must map to YymmddValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("261231", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "261231 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("260230", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "260230 must be rejected with a reason");
    }
}
