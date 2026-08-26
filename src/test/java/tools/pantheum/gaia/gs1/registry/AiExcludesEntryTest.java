package tools.pantheum.gaia.gs1.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AiExcludesEntry} — exact and range AI exclusions. */
@DisplayName("AiExcludesEntry")
class AiExcludesEntryTest {

    @Test
    @DisplayName("exact entry matches only its own AI")
    void exactEntry() {
        AiExcludesEntry e = AiExcludesEntry.ofExact("02");
        assertTrue(e.isExact());
        assertFalse(e.isRange());
        assertEquals("02", e.getExactAi());
        assertTrue(e.matches("02"));
        assertFalse(e.matches("03"));
    }

    @Test
    @DisplayName("range entry matches AIs within its bounds")
    void rangeEntry() {
        AiExcludesEntry e = AiExcludesEntry.ofRange("3100", "3105");
        assertTrue(e.isRange());
        assertEquals("3100", e.getRangeStart());
        assertEquals("3105", e.getRangeEnd());
        assertTrue(e.matches("3100"));
        assertTrue(e.matches("3103"));
        assertTrue(e.matches("3105"));
        assertFalse(e.matches("3110"));
    }
}
