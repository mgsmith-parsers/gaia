package tools.pantheum.gaia.datacarrier.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link DataCarrierRegistry} — AIM Code ID and ECI lookups. */
@DisplayName("DataCarrierRegistry")
class DataCarrierRegistryTest {

    @Test
    @DisplayName("registry is populated from datacarriers.json")
    void populated() {
        assertFalse(DataCarrierRegistry.BY_AIM_CODE_ID.isEmpty());
        assertFalse(DataCarrierRegistry.ECI_BY_INDICATOR.isEmpty());
    }

    @Test
    @DisplayName("resolves a known AIM Code ID")
    void knownAimCodeId() {
        assertTrue(DataCarrierRegistry.forAimCodeId("]A0").isPresent(),
                "]A0 (Code 39) must be a known AIM Code ID");
    }

    @Test
    @DisplayName("unknown AIM Code ID resolves to empty")
    void unknownAimCodeId() {
        assertTrue(DataCarrierRegistry.forAimCodeId("]_9").isEmpty());
    }

    @Test
    @DisplayName("resolves a known ECI indicator")
    void knownEci() {
        assertTrue(DataCarrierRegistry.eciForIndicator("\\000001").isPresent(),
                "ECI 000001 (ISO-8859-1) must be known");
    }
}
