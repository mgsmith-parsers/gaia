package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link NozeroPrefixValidator} — component format {@code nozeroprefix}. */
@DisplayName("NozeroPrefixValidator (nozeroprefix)")
class NozeroPrefixValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("nozeroprefix");

    @Test
    @DisplayName("is registered for format 'nozeroprefix'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'nozeroprefix'");
        assertTrue(validator instanceof NozeroPrefixValidator, "format 'nozeroprefix' must map to NozeroPrefixValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("123", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "123 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("012", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "012 must be rejected with a reason");
    }
}
