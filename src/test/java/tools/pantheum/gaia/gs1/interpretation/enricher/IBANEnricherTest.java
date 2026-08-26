package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link IBANEnricher} — interpretation enrichment for AI (8007). */
@DisplayName("IBANEnricher (AI 8007)")
class IBANEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("IBANEnricher").isPresent(),
                "IBANEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (8007) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("8020A\u001D41595060001343528007GB82WEST12345698765432");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("8007").getInterpretations().isEmpty(),
                "AI (8007) must carry interpretations produced in INTERPRETATION mode");
    }
}
