package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;

import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1AIObjectElement} — a single resolved AI element. */
@DisplayName("GS1AIObjectElement")
class GS1AIObjectElementTest {

    private GS1AIObjectElement gtinElement() {
        return new GS1AIObjectElement(
                AiDefinitionRegistry.getInstance().find("01").orElseThrow(),
                "09506000134352", 0, Collections.emptyList());
    }

    @Test
    @DisplayName("carries the AI code, title, and value from its definition")
    void definitionFields() {
        GS1AIObjectElement el = gtinElement();
        assertEquals("01", el.getAi());
        assertEquals("09506000134352", el.getValue());
        assertNotNull(el.getTitle());
    }

    @Test
    @DisplayName("error and warning lists are filtered by level")
    void errorWarningFiltering() {
        GS1AIObjectElement el = gtinElement();
        assertFalse(el.hasErrors());
        el.addError(ErrorRegistry.INSTANCE.create("GE-C003", "01", 0,
                Map.of("ai", "01", "value", "x")));
        el.addError(ErrorRegistry.INSTANCE.create("GE-W002", null, 0, Map.of()));
        assertTrue(el.hasErrors());
        assertTrue(el.hasWarnings());
        assertEquals(1, el.getErrors().size());
        assertEquals(1, el.getWarnings().size());
        assertEquals(2, el.getIssues().size());
    }

    @Test
    @DisplayName("addError(null) is rejected")
    void addNullErrorRejected() {
        assertThrows(IllegalArgumentException.class, () -> gtinElement().addError(null));
    }

    @Test
    @DisplayName("Digital Link AI role is optional: null by default, settable, clearable")
    void digitalLinkAiTypeOptional() {
        GS1AIObjectElement el = gtinElement();
        assertNull(el.getDigitalLinkAIType(), "No role assigned by default");
        assertFalse(el.hasDigitalLinkAIType());

        el.setDigitalLinkAIType(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY);
        assertEquals(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY, el.getDigitalLinkAIType());
        assertTrue(el.hasDigitalLinkAIType());

        el.setDigitalLinkAIType(null);
        assertNull(el.getDigitalLinkAIType(), "The role can be cleared");
        assertFalse(el.hasDigitalLinkAIType());
    }

    @Test
    @DisplayName("getInterpretation(null) is rejected; unknown type returns null")
    void getInterpretationContract() {
        GS1AIObjectElement el = gtinElement();
        assertThrows(IllegalArgumentException.class, () -> el.getInterpretation(null));
        assertNull(el.getInterpretation(GS1Constants_Enricher.GTIN_TYPE),
                "No interpretations attached yet");
    }
}
