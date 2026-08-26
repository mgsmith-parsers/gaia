package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1PrefixRegistry} — GS1 company prefix ranges. */
@DisplayName("GS1PrefixRegistry")
class GS1PrefixRegistryTest {

    @Test
    @DisplayName("recognises the GS1 demo prefix 9506000")
    void knownPrefix() {
        assertTrue(GS1PrefixRegistry.INSTANCE.isKnownPrefix("9506000134352"));
    }

    @Test
    @DisplayName("rejects a value that matches no prefix range")
    void unknownPrefix() {
        assertFalse(GS1PrefixRegistry.INSTANCE.isKnownPrefix("ABCDEFG"));
    }

    @Test
    @DisplayName("provides a description for a known prefix")
    void description() {
        String desc = GS1PrefixRegistry.INSTANCE.descriptionFor("9506000134352");
        assertNotNull(desc);
        assertFalse(desc.isBlank());
    }

    @Test
    @DisplayName("prefixMatchFor returns the matched prefix and description")
    void prefixMatch() {
        String[] m = GS1PrefixRegistry.INSTANCE.prefixMatchFor("9506000134352");
        assertNotNull(m);
        assertEquals("950", m[0]);
        assertFalse(m[1].isBlank());
    }

    @Test
    @DisplayName("a 3-digit range spanning the full last digit (start ends 0, end ends 9) yields a 2-digit prefix")
    void threeDigitFullSpanShortensToTwo() {
        // Range 030–039 (GS1 US) ends 0/9 across its final digit → significant prefix is "03".
        String[] m = GS1PrefixRegistry.INSTANCE.prefixMatchFor("0351234567890");
        assertNotNull(m);
        assertEquals("03", m[0]);
    }

    @Test
    @DisplayName("prefixMatchFor returns null for an unrecognised prefix")
    void noMatch() {
        assertNull(GS1PrefixRegistry.INSTANCE.prefixMatchFor("ABCDEFG"));
    }
}
