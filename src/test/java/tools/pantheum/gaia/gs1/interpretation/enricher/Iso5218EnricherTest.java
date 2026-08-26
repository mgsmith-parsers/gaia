package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link Iso5218Enricher} — interpretation enrichment for AI (7252). */
@DisplayName("Iso5218Enricher (AI 7252)")
class Iso5218EnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("Iso5218Enricher").isPresent(),
                "Iso5218Enricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7252) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8018950600012345678907\u001D72521");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7252").getInterpretations().isEmpty(),
                "AI (7252) must carry interpretations produced in INTERPRETATION mode");
    }
}
