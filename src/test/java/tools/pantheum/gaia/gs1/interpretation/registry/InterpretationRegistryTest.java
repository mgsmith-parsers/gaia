package tools.pantheum.gaia.gs1.interpretation.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link InterpretationRegistry} — per-AI interpretation definitions. */
@DisplayName("InterpretationRegistry")
class InterpretationRegistryTest {

    @Test
    @DisplayName("AI (00) has interpretation definitions")
    void knownAi() {
        List<InterpretationDefinition> defs = InterpretationRegistry.INSTANCE.find("00").orElseThrow();
        assertFalse(defs.isEmpty());
    }

    @Test
    @DisplayName("an AI without interpretations resolves to empty or an empty list")
    void aiWithoutInterpretations() {
        InterpretationRegistry.INSTANCE.find("10")
                .ifPresent(defs -> assertTrue(defs.isEmpty(),
                        "AI (10) defines no interpretations in ai-content.json"));
    }

    @Test
    @DisplayName("resolves enrichers by simple class name")
    void enricherLookup() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("GTINEnricher").isPresent());
    }
}
