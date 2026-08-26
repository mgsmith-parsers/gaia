package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GTINEnricher} — interpretation enrichment for AI (01). */
@DisplayName("GTINEnricher (AI 01)")
class GTINEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("GTINEnricher").isPresent(),
                "GTINEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (01) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("0109506000134352");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("01").getInterpretations().isEmpty(),
                "AI (01) must carry interpretations produced in INTERPRETATION mode");
    }

    @Test
    @DisplayName("emits the GTIN check digit (final digit) as an interpretation")
    void emitsCheckDigit() {
        ParseResult resp = parser.parse("0109506000134352");   // check digit is the final '2'
        String checkDigit = resp.getAiObject().get("01").getInterpretations().stream()
                .filter(i -> "GTIN_CHECK_DIGIT".equals(i.getType()))
                .map(tools.pantheum.gaia.gs1.model.GS1AIInterpretation::getValue)
                .findFirst()
                .orElse(null);
        assertEquals("2", checkDigit, "GTIN check digit interpretation must be the final digit");
    }
}
