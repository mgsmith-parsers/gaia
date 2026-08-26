package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link BirthSequenceEnricher} — interpretation enrichment for AI (7258). */
@DisplayName("BirthSequenceEnricher (AI 7258)")
class BirthSequenceEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("BirthSequenceEnricher").isPresent(),
                "BirthSequenceEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7258) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8018950600012345678907\u001D7259A\u001D72581/3");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7258").getInterpretations().isEmpty(),
                "AI (7258) must carry interpretations produced in INTERPRETATION mode");
    }
}
