package tools.pantheum.gaia.gs1.interpretation;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link InterpretationEngine} — element enrichment. */
@DisplayName("InterpretationEngine")
class InterpretationEngineTest {

    private final AISyntaxParser syntaxParser = new AISyntaxParser(AiDefinitionRegistry.getInstance());
    private final InterpretationEngine engine = new InterpretationEngine();
    private final ParseConfig config = ParseConfig.defaultConfig();

    @Test
    @DisplayName("attaches interpretations to a parsed element")
    void enrichesElements() {
        List<GS1AIObjectElement> els =
                syntaxParser.parse("0109506000134352", config).getElements();
        assertTrue(els.get(0).getInterpretations().isEmpty(), "No interpretations before enrichment");
        engine.interpret(els, config);
        assertFalse(els.get(0).getInterpretations().isEmpty(), "Interpretations after enrichment");
    }

    @Test
    @DisplayName("leaves elements without definitions untouched")
    void noDefinitionsNoInterpretations() {
        List<GS1AIObjectElement> els = syntaxParser.parse("10LOT1", config).getElements();
        engine.interpret(els, config);
        assertTrue(els.get(0).getInterpretations().isEmpty(),
                "AI (10) defines no interpretations in ai-content.json");
    }
}
