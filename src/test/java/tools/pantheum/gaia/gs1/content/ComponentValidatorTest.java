package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ComponentValidator} — per-component format checks (each format validator
 *  raises its own error, e.g. GE-C058 for HHMM). */
@DisplayName("ComponentValidator")
class ComponentValidatorTest {

    private final AISyntaxParser syntaxParser = new AISyntaxParser(AiDefinitionRegistry.getInstance());
    private final ParseConfig config = ParseConfig.defaultConfig();

    private GS1AIObjectElement elementFor(String input) {
        return syntaxParser.parse(input, config).getElements().get(0);
    }

    @Test
    @DisplayName("valid component values pass")
    void validComponentsPass() {
        GS1AIObjectElement el = elementFor("70112612311230");
        ComponentValidator.INSTANCE.validate(el, config);
        assertFalse(el.hasErrors());
    }

    @Test
    @DisplayName("an out-of-range time component gets its format error (GE-C058, HHMM)")
    void invalidComponentGetsFormatError() {
        GS1AIObjectElement el = elementFor("70112612319930"); // hour 99
        ComponentValidator.INSTANCE.validate(el, config);
        assertTrue(el.getErrors().stream().anyMatch(e -> "GE-C058".equals(e.getId())));
    }
}
