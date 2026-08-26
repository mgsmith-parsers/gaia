package tools.pantheum.gaia.gs1.content.componentformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.gs1.content.FormatValidators;

/** Tests for {@link PieceOfTotalValidator} — component format {@code pieceoftotal}. */
@DisplayName("PieceOfTotalValidator (pieceoftotal)")
class PieceOfTotalValidatorTest {

    private final ComponentFormatInterface validator = FormatValidators.forFormat("pieceoftotal");

    @Test
    @DisplayName("is registered for format 'pieceoftotal'")
    void registeredForFormat() {
        assertNotNull(validator, "FormatValidators must know format 'pieceoftotal'");
        assertTrue(validator instanceof PieceOfTotalValidator, "format 'pieceoftotal' must map to PieceOfTotalValidator");
    }

    @Test
    @DisplayName("accepts a valid value")
    void validValueAccepted() {
        assertTrue(validator.validate("0101", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "0101 must be accepted");
    }

    @Test
    @DisplayName("rejects an invalid value with a reason")
    void invalidValueRejected() {
        assertFalse(validator.validate("0100", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "0100 must be rejected with a reason");
    }
}
