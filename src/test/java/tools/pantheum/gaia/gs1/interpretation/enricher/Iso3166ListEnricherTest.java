package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link Iso3166ListEnricher} — interpretation enrichment for AI (423). */
@DisplayName("Iso3166ListEnricher (AI 423)")
class Iso3166ListEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("Iso3166ListEnricher").isPresent(),
                "Iso3166ListEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (423) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("0109506000134352423036");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("423").getInterpretations().isEmpty(),
                "AI (423) must carry interpretations produced in INTERPRETATION mode");
    }
}
