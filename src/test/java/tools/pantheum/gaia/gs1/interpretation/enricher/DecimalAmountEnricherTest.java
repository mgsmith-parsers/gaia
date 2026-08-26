package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link DecimalAmountEnricher} — interpretation enrichment for AI (3900). */
@DisplayName("DecimalAmountEnricher (AI 3900)")
class DecimalAmountEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("DecimalAmountEnricher").isPresent(),
                "DecimalAmountEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (3900) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("2559506000134352\u001D39001");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("3900").getInterpretations().isEmpty(),
                "AI (3900) must carry interpretations produced in INTERPRETATION mode");
    }
}
