package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GMNEnricher} — interpretation enrichment for AI (8013). */
@DisplayName("GMNEnricher (AI 8013)")
class GMNEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("GMNEnricher").isPresent(),
                "GMNEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8013) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("80139506000ABCAS");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8013").getInterpretations().isEmpty(),
                "AI (8013) must carry interpretations produced in INTERPRETATION mode");
    }
}
