package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1AIInterpretation} — a typed, labelled interpretation segment. */
@DisplayName("GS1AIInterpretation")
class GS1AIInterpretationTest {

    @Test
    @DisplayName("constructor populates type, label, and value")
    void constructorAndAccessors() {
        GS1AIInterpretation interp = new GS1AIInterpretation("testType", "Test Label", "42");
        assertEquals("testType", interp.getType());
        assertEquals("Test Label", interp.getLabel());
        assertEquals("42", interp.getValue());
        assertTrue(interp.toString().contains("42"));
    }

    @Test
    @DisplayName("a parsed GTIN element carries typed interpretations")
    void producedByPipeline() {
        ParseResult resp = new GaiaParser().parse("0109506000134352");
        GS1AIInterpretation interp = resp.getAiObject().get("01")
                .getInterpretation(GS1Constants_Enricher.GTIN_TYPE);
        assertNotNull(interp, "The GTIN format interpretation must be produced");
        assertEquals(GS1Constants_Enricher.GTIN_TYPE, interp.getType());
        assertNotNull(interp.getLabel());
        assertNotNull(interp.getValue());
    }
}
