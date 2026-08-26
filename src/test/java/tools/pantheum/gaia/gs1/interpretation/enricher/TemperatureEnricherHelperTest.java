package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;

/**
 * Tests for {@link TemperatureEnricherHelper} — shared decimal/temperature decomposition
 * used by {@link TemperatureFahrenheitEnricher} (AI 4330) and
 * {@link TemperatureCelsiusEnricher} (AI 4331).
 */
@DisplayName("TemperatureEnricherHelper")
class TemperatureEnricherHelperTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("supports Fahrenheit enrichment (AI 4330)")
    void fahrenheitEnrichment() {
        ParseResult resp = parser.parse("000950600013435211134330111111");
        assertTrue(resp.isValid());
        assertFalse(resp.getAiObject().get("4330").getInterpretations().isEmpty(),
                "AI (4330) must carry temperature interpretations");
    }

    @Test
    @DisplayName("supports Celsius enrichment (AI 4331)")
    void celsiusEnrichment() {
        ParseResult resp = parser.parse("000950600013435211134331111111");
        assertTrue(resp.isValid());
        assertFalse(resp.getAiObject().get("4331").getInterpretations().isEmpty(),
                "AI (4331) must carry temperature interpretations");
    }
}
