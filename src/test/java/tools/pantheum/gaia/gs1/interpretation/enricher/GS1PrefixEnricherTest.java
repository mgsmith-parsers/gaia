package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GS1PrefixEnricher} — interpretation enrichment for AI (00). */
@DisplayName("GS1PrefixEnricher (AI 00)")
class GS1PrefixEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("GS1PrefixEnricher").isPresent(),
                "GS1PrefixEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (00) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("00095060001343521113");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("00").getInterpretations().isEmpty(),
                "AI (00) must carry interpretations produced in INTERPRETATION mode");
    }
}
