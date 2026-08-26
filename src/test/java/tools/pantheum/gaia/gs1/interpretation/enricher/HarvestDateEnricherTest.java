package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link HarvestDateEnricher} — interpretation enrichment for AI (7007). */
@DisplayName("HarvestDateEnricher (AI 7007)")
class HarvestDateEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("HarvestDateEnricher").isPresent(),
                "HarvestDateEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7007) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("01095060001343527007261231");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7007").getInterpretations().isEmpty(),
                "AI (7007) must carry interpretations produced in INTERPRETATION mode");
    }
}
