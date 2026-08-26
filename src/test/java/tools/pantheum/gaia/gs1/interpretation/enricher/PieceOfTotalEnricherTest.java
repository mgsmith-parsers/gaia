package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link PieceOfTotalEnricher} — interpretation enrichment for AI (8006). */
@DisplayName("PieceOfTotalEnricher (AI 8006)")
class PieceOfTotalEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("PieceOfTotalEnricher").isPresent(),
                "PieceOfTotalEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8006) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8006095060001343520101");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8006").getInterpretations().isEmpty(),
                "AI (8006) must carry interpretations produced in INTERPRETATION mode");
    }
}
