package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link YesNoEnricher} — interpretation enrichment for AI (4321). */
@DisplayName("YesNoEnricher (AI 4321)")
class YesNoEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("YesNoEnricher").isPresent(),
                "YesNoEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4321) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("0009506000134352111343211");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4321").getInterpretations().isEmpty(),
                "AI (4321) must carry interpretations produced in INTERPRETATION mode");
    }
}
