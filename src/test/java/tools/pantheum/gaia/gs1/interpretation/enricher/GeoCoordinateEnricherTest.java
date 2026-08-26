package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GeoCoordinateEnricher} — interpretation enrichment for AI (4309). */
@DisplayName("GeoCoordinateEnricher (AI 4309)")
class GeoCoordinateEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("GeoCoordinateEnricher").isPresent(),
                "GeoCoordinateEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4309) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("00095060001343521113430900000000000000000000");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4309").getInterpretations().isEmpty(),
                "AI (4309) must carry interpretations produced in INTERPRETATION mode");
    }
}
