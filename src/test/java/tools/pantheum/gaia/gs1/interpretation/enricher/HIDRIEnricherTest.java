package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link HIDRIEnricher} — interpretation enrichment for AI (8014). */
@DisplayName("HIDRIEnricher (AI 8014)")
class HIDRIEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("HIDRIEnricher").isPresent(),
                "HIDRIEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8014) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("010950600013435280149506000ABCAS");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8014").getInterpretations().isEmpty(),
                "AI (8014) must carry interpretations produced in INTERPRETATION mode");
    }
}
