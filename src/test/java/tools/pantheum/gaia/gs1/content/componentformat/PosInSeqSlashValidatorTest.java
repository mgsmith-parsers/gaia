package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link PosInSeqSlashValidator} — component format {@code posinseqslash}. */
@DisplayName("PosInSeqSlashValidator (posinseqslash)")
class PosInSeqSlashValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("posinseqslash");

    @Test
    @DisplayName("is registered for format 'posinseqslash'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'posinseqslash'");
        assertTrue(validator instanceof PosInSeqSlashValidator, "format 'posinseqslash' must map to PosInSeqSlashValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("1/3", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "1/3 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("3/1", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "3/1 must be rejected with a reason");
    }
}
