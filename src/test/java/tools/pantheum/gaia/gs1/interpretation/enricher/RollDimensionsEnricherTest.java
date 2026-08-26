package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link RollDimensionsEnricher} — interpretation enrichment for AI (8001). */
@DisplayName("RollDimensionsEnricher (AI 8001)")
class RollDimensionsEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("RollDimensionsEnricher").isPresent(),
                "RollDimensionsEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8001) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("0109506000134352800111111111111101");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8001").getInterpretations().isEmpty(),
                "AI (8001) must carry interpretations produced in INTERPRETATION mode");
    }
}
