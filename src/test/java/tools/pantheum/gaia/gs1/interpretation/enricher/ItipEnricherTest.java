package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.result.ParseResult;

import java.util.List;

/** Tests for {@link ItipEnricher} — GS1 prefix enrichment for AI (8006) and AI (8026). */
@DisplayName("ItipEnricher (AI 8006 / 8026)")
class ItipEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    private static final String GS = String.valueOf((char) 0x1D);

    /** GTIN-14 09506000134352 — indicator "0", GS1 company prefix under member code 950. */
    private static final String ITIP_VALUE = "095060001343520101";

    private static String valueOf(List<GS1AIInterpretation> interpretations, String type) {
        return interpretations.stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst()
                .orElse(null);
    }

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("ItipEnricher").isPresent(),
                "ItipEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("resolves the member organisation for AI (8006), skipping the indicator digit")
    void enrichesAi8006() {
        ParseResult resp = parser.parse("8006" + ITIP_VALUE);
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("8006").getInterpretations();
        assertEquals("950", valueOf(interpretations, GS1Constants_Enricher.GS1_MEMBER_CODE),
                "Prefix must be matched after the GTIN-14 indicator digit, not on the raw component");
        assertEquals("GS1 Global Office", valueOf(interpretations, GS1Constants_Enricher.GS1_MEMBER_NAME));
    }

    @Test
    @DisplayName("resolves the member organisation for AI (8026), skipping the indicator digit")
    void enrichesAi8026() {
        // AI (8026) requires (00 AND 37) to be present in the element string.
        ParseResult resp = parser.parse("00006141411234567890" + "371" + GS + "8026" + ITIP_VALUE);
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("8026").getInterpretations();
        assertEquals("950", valueOf(interpretations, GS1Constants_Enricher.GS1_MEMBER_CODE),
                "Prefix must be matched after the GTIN-14 indicator digit, not on the raw component");
        assertEquals("GS1 Global Office", valueOf(interpretations, GS1Constants_Enricher.GS1_MEMBER_NAME));
    }
}
