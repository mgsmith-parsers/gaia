package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link Iso3166Enricher} — interpretation enrichment for AI (421). */
@DisplayName("Iso3166Enricher (AI 421)")
class Iso3166EnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("Iso3166Enricher").isPresent(),
                "Iso3166Enricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (421) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("421036A");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("421").getInterpretations().isEmpty(),
                "AI (421) must carry interpretations produced in INTERPRETATION mode");
    }
}
