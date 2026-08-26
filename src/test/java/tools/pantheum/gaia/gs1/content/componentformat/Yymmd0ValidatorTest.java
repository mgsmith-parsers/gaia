package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link Yymmd0Validator} — component format {@code yymmd0}. */
@DisplayName("Yymmd0Validator (yymmd0)")
class Yymmd0ValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("yymmd0");

    @Test
    @DisplayName("is registered for format 'yymmd0'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'yymmd0'");
        assertTrue(validator instanceof Yymmd0Validator, "format 'yymmd0' must map to Yymmd0Validator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("261200", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "261200 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("261300", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "261300 must be rejected with a reason");
    }
}
