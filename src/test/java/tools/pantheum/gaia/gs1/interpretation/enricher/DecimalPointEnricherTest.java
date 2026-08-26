package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link DecimalPointEnricher} — interpretation enrichment for AI (3100). */
@DisplayName("DecimalPointEnricher (AI 3100)")
class DecimalPointEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("DecimalPointEnricher").isPresent(),
                "DecimalPointEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (3100) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("01095060001343523100111111");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("3100").getInterpretations().isEmpty(),
                "AI (3100) must carry interpretations produced in INTERPRETATION mode");
    }
}
