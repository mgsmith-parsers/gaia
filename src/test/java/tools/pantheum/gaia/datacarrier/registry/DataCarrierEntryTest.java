package tools.pantheum.gaia.datacarrier.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link DataCarrierEntry} — registry entry accessors. */
@DisplayName("DataCarrierEntry")
class DataCarrierEntryTest {

    @Test
    @DisplayName("a registry entry exposes its AIM metadata")
    void accessors() {
        DataCarrierEntry entry = DataCarrierRegistry.forAimCodeId("]A0").orElseThrow();
        assertEquals("]A0", entry.getAimCodeId());
        assertEquals("A", entry.getCodeChar());
        assertNotNull(entry.getModifier());
        assertNotNull(entry.getName());
        assertFalse(entry.getName().isBlank());
    }
}
