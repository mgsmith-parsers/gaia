package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validators whose value has independent sub-fields report one error per failing
 * field via {@link tools.pantheum.gaia.gs1.content.ComponentFormatInterface#validate}.
 */
@DisplayName("multi-error component-format validators")
class MultiErrorValidatorTest {

    private static List<GaiaError> errors(ComponentFormatInterface validator, String value) {
        return validator.validate(value, "4309", 0, "comp", "fmt", GaiaConstants.Language.ENGLISH);
    }

    @Test
    @DisplayName("lat/long out of range on both halves yields two errors")
    void latLongBothHalves() {
        assertEquals(2, errors(LatLongValidator.INSTANCE, "99999999999999999999").size());
        assertTrue(errors(LatLongValidator.INSTANCE, "18000000003600000000").isEmpty(),
                "a valid lat/long yields no errors");
    }

    @Test
    @DisplayName("piece-of-total out of range on both fields yields two errors")
    void pieceOfTotalBothFields() {
        assertEquals(2, errors(PieceOfTotalValidator.INSTANCE, "0000").size());
    }

    @Test
    @DisplayName("ISO 3166 list reports each invalid country code separately")
    void iso3166ListEachCode() {
        assertEquals(2, errors(Iso3166ListValidator.INSTANCE, "000000").size());
    }
}
