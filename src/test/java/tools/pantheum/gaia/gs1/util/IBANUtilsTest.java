package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link IBANUtils} — ISO 13616 mod-97 verification. */
@DisplayName("IBANUtils")
class IBANUtilsTest {

    @Test
    @DisplayName("a valid IBAN passes mod-97")
    void validIban() {
        assertTrue(IBANUtils.verifyIbanMod97("GB82WEST12345698765432"));
    }

    @Test
    @DisplayName("a corrupted IBAN fails mod-97")
    void corruptedIban() {
        assertFalse(IBANUtils.verifyIbanMod97("GB82WEST12345698765433"));
    }
}
