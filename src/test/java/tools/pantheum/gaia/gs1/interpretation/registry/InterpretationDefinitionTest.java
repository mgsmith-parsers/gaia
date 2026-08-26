package tools.pantheum.gaia.gs1.interpretation.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link InterpretationDefinition} — definition bean loaded from ai-content.json. */
@DisplayName("InterpretationDefinition")
class InterpretationDefinitionTest {

    @Test
    @DisplayName("AI (00) definitions carry an enricher reference")
    void loadedDefinition() {
        List<InterpretationDefinition> defs = InterpretationRegistry.INSTANCE.find("00").orElseThrow();
        assertTrue(defs.stream().anyMatch(d -> "SSCCEnricher".equals(d.getEnricher())),
                "AI (00) must reference SSCCEnricher");
    }

    @Test
    @DisplayName("setters mirror getters")
    void settersAndGetters() {
        InterpretationDefinition def = new InterpretationDefinition();
        def.setType("testType");
        def.setLabel("Test Label");
        assertEquals("testType", def.getType());
        assertEquals("Test Label", def.getLabel());
    }
}
