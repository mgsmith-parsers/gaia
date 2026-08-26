package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link ProductionTimeEnricher} — interpretation enrichment for AI (8008). */
@DisplayName("ProductionTimeEnricher (AI 8008)")
class ProductionTimeEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("ProductionTimeEnricher").isPresent(),
                "ProductionTimeEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8008) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("0109506000134352800826123123");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8008").getInterpretations().isEmpty(),
                "AI (8008) must carry interpretations produced in INTERPRETATION mode");
    }
}
