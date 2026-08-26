package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link CpidEnricher} — interpretation enrichment for AI (8010). */
@DisplayName("CpidEnricher (AI 8010)")
class CpidEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("CpidEnricher").isPresent(),
                "CpidEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8010) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("80109506000ABC");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8010").getInterpretations().isEmpty(),
                "AI (8010) must carry interpretations produced in INTERPRETATION mode");
    }
}
