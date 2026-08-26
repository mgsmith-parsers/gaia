package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1Constants} — AI codes, FNC1, and interpretation keys. */
@DisplayName("GS1Constants")
class GS1ConstantsTest {

    @Test
    @DisplayName("FNC1_GS is the ASCII Group Separator (0x1D)")
    void fnc1IsGroupSeparator() {
        assertEquals((char) 0x1D, GS1Constants.FNC1_GS);
    }

    @Test
    @DisplayName("AI constants hold their numeric codes")
    void aiConstantValues() {
        assertEquals("00", GS1Constants_AICodes.AI_00_SSCC);
        assertEquals("01", GS1Constants_AICodes.AI_01_GTIN);
        assertEquals("21", GS1Constants_AICodes.AI_21_SERIAL);
        assertEquals("8200", GS1Constants_AICodes.AI_8200_PRODUCT_URL);
    }

    @Test
    @DisplayName("interpretation type keys are defined")
    void interpretationTypeKeys() {
        assertNotNull(GS1Constants_Enricher.GTIN_TYPE);
        assertEquals("GTIN_TYPE", GS1Constants_Enricher.GTIN_TYPE);
    }

    @Test
    @DisplayName("ParseMode declares the three pipeline depths")
    void parseModeValues() {
        assertNotNull(GS1Constants.ParseMode.valueOf("SYNTAX"));
        assertNotNull(GS1Constants.ParseMode.valueOf("CONTENT"));
        assertNotNull(GS1Constants.ParseMode.valueOf("INTERPRETATION"));
    }

    @Test
    @DisplayName("DigitalLinkAIType declares the three Digital Link AI roles")
    void digitalLinkAiTypeValues() {
        assertNotNull(GS1Constants.DigitalLinkAIType.valueOf("PRIMARY_IDENTIFICATION_KEY"));
        assertNotNull(GS1Constants.DigitalLinkAIType.valueOf("KEY_QUALIFIER"));
        assertNotNull(GS1Constants.DigitalLinkAIType.valueOf("DATA_ATTRIBUTE"));
        assertEquals(3, GS1Constants.DigitalLinkAIType.values().length);
    }
}
