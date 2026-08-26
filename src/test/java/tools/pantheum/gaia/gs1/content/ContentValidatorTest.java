package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ContentValidator} — the content-stage orchestrator. */
@DisplayName("ContentValidator")
class ContentValidatorTest {

    private final AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
    private final AISyntaxParser syntaxParser = new AISyntaxParser(registry);
    private final ContentValidator validator = new ContentValidator(registry);
    private final ParseConfig config = ParseConfig.defaultConfig();

    @Test
    @DisplayName("a fully valid element passes all stages")
    void validElementPasses() {
        List<GS1AIObjectElement> els = syntaxParser.parse("0109506000134352", config).getElements();
        validator.validate(els, config);
        assertFalse(els.get(0).hasErrors());
    }

    @Test
    @DisplayName("a wrong check digit produces GE-C003")
    void checkDigitFailure() {
        List<GS1AIObjectElement> els = syntaxParser.parse("0109506000134353", config).getElements();
        validator.validate(els, config);
        assertTrue(els.get(0).getErrors().stream().anyMatch(e -> "GE-C003".equals(e.getId())));
    }

    @Test
    @DisplayName("a regex failure stops later checks (single error)")
    void regexFailureShortCircuits() {
        List<GS1AIObjectElement> els = syntaxParser.parse("11999999", config).getElements();
        validator.validate(els, config);
        assertEquals(1, els.get(0).getErrors().size(),
                "Only the regex error (GE-C001) is expected — later stages are skipped");
    }
}
