package tools.pantheum.gaia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GaiaConstants} — shared enums. */
@DisplayName("GaiaConstants")
class GaiaConstantsTest {

    @Test
    @DisplayName("ParseMode declares all four pipeline depths")
    void parseModeValues() {
        assertNotNull(GaiaConstants.ParseMode.valueOf("SYNTAX"));
        assertNotNull(GaiaConstants.ParseMode.valueOf("CONTENT"));
        assertNotNull(GaiaConstants.ParseMode.valueOf("DATA_CARRIER"));
        assertNotNull(GaiaConstants.ParseMode.valueOf("INTERPRETATION"));
    }

    @Test
    @DisplayName("ErrorLevel distinguishes warnings from errors")
    void errorLevelValues() {
        assertNotEquals(GaiaConstants.ErrorLevel.valueOf("WARNING").name(), "");
        assertTrue(GaiaConstants.ErrorLevel.values().length >= 2,
                "At least WARNING and one error level are required");
    }

    @Test
    @DisplayName("DateSeparator symbols match their names")
    void dateSeparatorSymbols() {
        assertEquals("/", GaiaConstants.DateSeparator.SLASH.symbol());
        assertEquals("-", GaiaConstants.DateSeparator.HYPHEN.symbol());
        assertEquals(".", GaiaConstants.DateSeparator.PERIOD.symbol());
    }

    @Test
    @DisplayName("Language declares English and French")
    void languageValues() {
        assertNotNull(GaiaConstants.Language.valueOf("ENGLISH"));
        assertNotNull(GaiaConstants.Language.valueOf("FRENCH"));
    }
}
