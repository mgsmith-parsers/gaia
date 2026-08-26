package tools.pantheum.gaia.gs1.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AiDefinitionRegistry} — the AI definition lookup. */
@DisplayName("AiDefinitionRegistry")
class AiDefinitionRegistryTest {

    private final AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();

    @Test
    @DisplayName("loads all 541 AI definitions")
    void allDefinitionsLoaded() {
        assertEquals(541, registry.size());
    }

    @Test
    @DisplayName("finds known AIs and rejects unknown ones")
    void findAndIsKnown() {
        assertTrue(registry.find("01").isPresent());
        assertTrue(registry.isKnown("8200"));
        assertTrue(registry.find("XX").isEmpty());
        assertFalse(registry.isKnown("0000"));
    }

    @Test
    @DisplayName("maps two-digit prefixes to AI code lengths")
    void aiLengthForPrefix() {
        assertEquals(2, AiDefinitionRegistry.aiLengthForPrefix("01"));
        assertEquals(4, AiDefinitionRegistry.aiLengthForPrefix("31"));
        assertEquals(3, AiDefinitionRegistry.aiLengthForPrefix("25"));
        assertEquals(-1, AiDefinitionRegistry.aiLengthForPrefix("xy"));
    }
}
