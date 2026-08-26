package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link CertificationReferenceEnricher} — interpretation enrichment for AI (7230). */
@DisplayName("CertificationReferenceEnricher (AI 7230)")
class CertificationReferenceEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("CertificationReferenceEnricher").isPresent(),
                "CertificationReferenceEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7230) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("01095060001343527230EM1234");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7230").getInterpretations().isEmpty(),
                "AI (7230) must carry interpretations produced in INTERPRETATION mode");
    }
}
