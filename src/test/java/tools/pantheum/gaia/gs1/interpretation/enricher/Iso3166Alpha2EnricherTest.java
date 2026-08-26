package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link Iso3166Alpha2Enricher} — interpretation enrichment for AI (4307). */
@DisplayName("Iso3166Alpha2Enricher (AI 4307)")
class Iso3166Alpha2EnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("Iso3166Alpha2Enricher").isPresent(),
                "Iso3166Alpha2Enricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4307) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("000950600013435211134307AU");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4307").getInterpretations().isEmpty(),
                "AI (4307) must carry interpretations produced in INTERPRETATION mode");
    }
}
