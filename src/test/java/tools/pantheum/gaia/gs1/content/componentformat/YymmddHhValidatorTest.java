package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link YymmddHhValidator} — component format {@code yymmddhh}. */
@DisplayName("YymmddHhValidator (yymmddhh)")
class YymmddHhValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("yymmddhh");

    @Test
    @DisplayName("is registered for format 'yymmddhh'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'yymmddhh'");
        assertTrue(validator instanceof YymmddHhValidator, "format 'yymmddhh' must map to YymmddHhValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("26123123", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "26123123 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("26123124", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "26123124 must be rejected with a reason");
    }
}
