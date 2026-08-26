package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link MonetaryAmountEnricher} — interpretation enrichment for AI (3910). */
@DisplayName("MonetaryAmountEnricher (AI 3910)")
class MonetaryAmountEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("MonetaryAmountEnricher").isPresent(),
                "MonetaryAmountEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (3910) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8020A\u001D415950600013435239100361");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("3910").getInterpretations().isEmpty(),
                "AI (3910) must carry interpretations produced in INTERPRETATION mode");
    }
}
