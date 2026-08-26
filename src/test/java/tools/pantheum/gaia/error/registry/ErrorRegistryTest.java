package tools.pantheum.gaia.error.registry;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ErrorRegistry} — error creation and message templating. */
@DisplayName("ErrorRegistry")
class ErrorRegistryTest {

    @Test
    @DisplayName("creates a known error with substituted parameters")
    void parameterSubstitution() {
        GaiaError err = ErrorRegistry.INSTANCE.create("GE-S004", "10", 3, Map.of("ai", "10"));
        assertEquals("GE-S004", err.getId());
        assertTrue(err.getMessage().contains("10"),
                "The {ai} parameter must be substituted into the message");
    }

    @Test
    @DisplayName("produces French messages when requested")
    void frenchLanguage() {
        GaiaError en = ErrorRegistry.INSTANCE.create("GE-S004", "10", 0, Map.of("ai", "10"),
                GaiaConstants.Language.ENGLISH);
        GaiaError fr = ErrorRegistry.INSTANCE.create("GE-S004", "10", 0, Map.of("ai", "10"),
                GaiaConstants.Language.FRENCH);
        assertNotNull(en.getMessage());
        assertNotNull(fr.getMessage());
        assertNotEquals(en.getMessage(), fr.getMessage(),
                "English and French messages must differ");
    }
}
