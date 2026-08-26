package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AsfisEntry} — species record accessors. */
@DisplayName("AsfisEntry")
class AsfisEntryTest {

    @Test
    @DisplayName("a species entry exposes its taxonomy")
    void accessors() {
        AsfisEntry entry = AsfisData.entryFor("GXM").orElseThrow();
        assertEquals("GXM", entry.getAlpha3Code());
        assertNotNull(entry.getScientificName());
        assertFalse(entry.getScientificName().isBlank());
    }
}
