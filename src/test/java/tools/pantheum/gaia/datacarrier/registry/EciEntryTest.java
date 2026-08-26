package tools.pantheum.gaia.datacarrier.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link EciEntry} — ECI indicator entries. */
@DisplayName("EciEntry")
class EciEntryTest {

    @Test
    @DisplayName("a registry ECI entry exposes indicator, number, and charset")
    void accessors() {
        EciEntry eci = DataCarrierRegistry.eciForIndicator("\\000001").orElseThrow();
        assertEquals("\\000001", eci.getIndicator());
        assertEquals(1, eci.getNumber());
        assertEquals("ISO-8859-1", eci.getCharset());
        assertTrue(eci.toString().contains("1"));
    }
}
