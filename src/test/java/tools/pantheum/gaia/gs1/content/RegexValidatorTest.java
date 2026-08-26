package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link RegexValidator} — GE-C001 format checking. */
@DisplayName("RegexValidator")
class RegexValidatorTest {

    private final AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
    private final AISyntaxParser syntaxParser = new AISyntaxParser(registry);
    private final ParseConfig config = ParseConfig.defaultConfig();

    private GS1AIObjectElement elementFor(String input) {
        return syntaxParser.parse(input, config).getElements().get(0);
    }

    @Test
    @DisplayName("a value matching the AI regex passes")
    void matchingValuePasses() {
        GS1AIObjectElement el = elementFor("11261231");
        RegexValidator.INSTANCE.validate(el, registry.find("11").orElseThrow(), config);
        assertFalse(el.hasErrors());
    }

    @Test
    @DisplayName("a value failing the AI regex gets GE-C001")
    void failingValueGetsGeC001() {
        GS1AIObjectElement el = elementFor("11999999"); // month 99 fails the date regex
        RegexValidator.INSTANCE.validate(el, registry.find("11").orElseThrow(), config);
        assertTrue(el.getErrors().stream().anyMatch(e -> "GE-C001".equals(e.getId())));
    }
}
