package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link AIDCMediaTypeEnricher} — interpretation enrichment for AI (7241). */
@DisplayName("AIDCMediaTypeEnricher (AI 7241)")
class AIDCMediaTypeEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("AIDCMediaTypeEnricher").isPresent(),
                "AIDCMediaTypeEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7241) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8017950600012345678907\u001D724101");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7241").getInterpretations().isEmpty(),
                "AI (7241) must carry interpretations produced in INTERPRETATION mode");
    }
}
