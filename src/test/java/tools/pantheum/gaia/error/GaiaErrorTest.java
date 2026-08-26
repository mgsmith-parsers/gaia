package tools.pantheum.gaia.error;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GaiaError} — created through {@link ErrorRegistry}. */
@DisplayName("GaiaError")
class GaiaErrorTest {

    @Test
    @DisplayName("carries id, ai, position, level, and message")
    void accessors() {
        GaiaError err = ErrorRegistry.INSTANCE.create("GE-S004", "01", 5, Map.of("ai", "01"));
        assertEquals("GE-S004", err.getId());
        assertEquals("01", err.getAi());
        assertEquals(5, err.getPosition());
        assertNotNull(err.getLevel());
        assertNotNull(err.getCode());
        assertNotNull(err.getMessage());
        assertFalse(err.getMessage().isBlank());
    }

    @Test
    @DisplayName("warnings carry WARNING level")
    void warningLevel() {
        GaiaError warn = ErrorRegistry.INSTANCE.create("GE-W002", null, 0, Map.of());
        assertEquals(GaiaConstants.ErrorLevel.WARNING, warn.getLevel());
    }

    @Test
    @DisplayName("toString includes the error id")
    void toStringIncludesId() {
        GaiaError err = ErrorRegistry.INSTANCE.create("GE-S004", "01", 0, Map.of("ai", "01"));
        assertTrue(err.toString().contains("GE-S004"));
    }
}
