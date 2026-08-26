package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.GaiaConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests for {@link ComponentFormatInterface} — implementations return
 * an empty list for valid values and one or more errors otherwise.
 */
@DisplayName("ComponentFormatInterface contract")
class ComponentFormatInterfaceTest {

    @Test
    @DisplayName("a registered implementation reports no errors for a valid value")
    void emptyOnValid() {
        ComponentFormatInterface v = FormatValidators.forFormat("yymmdd");
        assertNotNull(v);
        assertTrue(v.validate("261231", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "Valid value must yield no errors");
    }

    @Test
    @DisplayName("a registered implementation reports an error on invalid input")
    void errorOnInvalid() {
        ComponentFormatInterface v = FormatValidators.forFormat("yymmdd");
        assertFalse(v.validate("260230", "00", 0, "c", "f", GaiaConstants.Language.ENGLISH).isEmpty(),
                "Invalid value must yield an error");
    }
}
