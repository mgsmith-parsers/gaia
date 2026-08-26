package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ASCIIRevealerUtils} — control character display helper. */
@DisplayName("ASCIIRevealerUtils")
class ASCIIRevealerUtilsTest {

    @Test
    @DisplayName("names the GS control character")
    void namesGs() {
        assertTrue(ASCIIRevealerUtils.nameOf((char) 0x1D).toUpperCase().contains("GS"));
    }

    @Test
    @DisplayName("printable() reveals control characters in a string")
    void printableRevealsControls() {
        String revealed = ASCIIRevealerUtils.printable("ABCD");
        assertNotEquals("ABCD", revealed, "The GS character must be replaced");
        assertTrue(revealed.contains("AB"));
        assertTrue(revealed.contains("CD"));
    }

    @Test
    @DisplayName("printable() leaves plain text unchanged")
    void printablePlainText() {
        assertEquals("ABC123", ASCIIRevealerUtils.printable("ABC123"));
    }
}
