package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link TemperatureFahrenheitEnricher} — interpretation enrichment for AI (4330). */
@DisplayName("TemperatureFahrenheitEnricher (AI 4330)")
class TemperatureFahrenheitEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("TemperatureFahrenheitEnricher").isPresent(),
                "TemperatureFahrenheitEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4330) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("000950600013435211134330111111");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4330").getInterpretations().isEmpty(),
                "AI (4330) must carry interpretations produced in INTERPRETATION mode");
    }
}
