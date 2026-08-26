package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AsfisData} — FAO ASFIS aquatic species dataset. */
@DisplayName("AsfisData")
class AsfisDataTest {

    @Test
    @DisplayName("the dataset is loaded")
    void loaded() {
        assertFalse(AsfisData.SPECIES.isEmpty());
    }

    @Test
    @DisplayName("a known alpha-3 code resolves to an entry")
    void knownCode() {
        assertTrue(AsfisData.entryFor("GXM").isPresent());
    }

    @Test
    @DisplayName("unknown codes resolve to empty")
    void unknownCode() {
        assertTrue(AsfisData.entryFor("ZZZ").isEmpty());
    }
}
