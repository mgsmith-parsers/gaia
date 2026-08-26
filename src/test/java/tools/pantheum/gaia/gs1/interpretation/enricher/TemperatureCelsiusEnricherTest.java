package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link TemperatureCelsiusEnricher} — interpretation enrichment for AI (4331). */
@DisplayName("TemperatureCelsiusEnricher (AI 4331)")
class TemperatureCelsiusEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("TemperatureCelsiusEnricher").isPresent(),
                "TemperatureCelsiusEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4331) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("000950600013435211134331111111");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4331").getInterpretations().isEmpty(),
                "AI (4331) must carry interpretations produced in INTERPRETATION mode");
    }
}
