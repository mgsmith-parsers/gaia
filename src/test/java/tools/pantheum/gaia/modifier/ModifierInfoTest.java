package tools.pantheum.gaia.modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ModifierInfo} — accessors, immutability, and the modified flag. */
@DisplayName("ModifierInfo")
class ModifierInfoTest {

    @Test
    @DisplayName("accessors return what was supplied")
    void accessors() {
        ModifierInfo info = new ModifierInfo("SCAN:01", "01", List.of("StripScanPrefix"));

        assertEquals("SCAN:01", info.getOriginalInput());
        assertEquals("01", info.getModifiedInput());
        assertEquals(List.of("StripScanPrefix"), info.getAppliedModifiers());
        assertTrue(info.isModified());
    }

    @Test
    @DisplayName("empty applied list means not modified")
    void notModified() {
        assertFalse(new ModifierInfo("01", "01", List.of()).isModified());
    }

    @Test
    @DisplayName("null applied list is normalised to empty")
    void nullAppliedList() {
        ModifierInfo info = new ModifierInfo("01", "01", null);

        assertNotNull(info.getAppliedModifiers());
        assertTrue(info.getAppliedModifiers().isEmpty());
        assertFalse(info.isModified());
    }

    @Test
    @DisplayName("applied list is unmodifiable and defensively read")
    void unmodifiableAppliedList() {
        List<String> source = new ArrayList<>(List.of("A"));
        ModifierInfo info = new ModifierInfo("x", "y", source);

        assertThrows(UnsupportedOperationException.class, () -> info.getAppliedModifiers().add("B"));

        source.add("B");   // mutating the source must not leak into the info
        assertEquals(List.of("A"), info.getAppliedModifiers());
    }
}
