package tools.pantheum.gaia.gs1.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AiRequiresEntry} — single, group, and range AI requirements. */
@DisplayName("AiRequiresEntry")
class AiRequiresEntryTest {

    @Test
    @DisplayName("single entry is satisfied by exactly its AI")
    void singleEntry() {
        AiRequiresEntry e = AiRequiresEntry.ofSingle("255");
        assertTrue(e.isSingle());
        assertTrue(e.isSatisfiedBy(Set.of("255", "01")));
        assertFalse(e.isSatisfiedBy(Set.of("01")));
    }

    @Test
    @DisplayName("group entry requires every member")
    void groupEntry() {
        AiRequiresEntry e = AiRequiresEntry.ofGroup(List.of("01", "21"));
        assertTrue(e.isGroup());
        assertTrue(e.isSatisfiedBy(Set.of("01", "21", "10")));
        assertFalse(e.isSatisfiedBy(Set.of("01")), "Both group members are required");
    }

    @Test
    @DisplayName("range entry is satisfied by any AI in range")
    void rangeEntry() {
        AiRequiresEntry e = AiRequiresEntry.ofRange("3100", "3105");
        assertTrue(e.isRange());
        assertTrue(e.isSatisfiedBy(Set.of("3103")));
        assertFalse(e.isSatisfiedBy(Set.of("3110")));
    }
}
