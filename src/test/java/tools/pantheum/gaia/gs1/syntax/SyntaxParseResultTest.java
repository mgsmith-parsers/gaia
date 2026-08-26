package tools.pantheum.gaia.gs1.syntax;

import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link SyntaxParseResult} — tokenisation outcome container. */
@DisplayName("SyntaxParseResult")
class SyntaxParseResultTest {

    private final AISyntaxParser parser = new AISyntaxParser(AiDefinitionRegistry.getInstance());
    private final ParseConfig config = ParseConfig.defaultConfig();

    @Test
    @DisplayName("clean input: elements populated, no syntax errors")
    void cleanResult() {
        SyntaxParseResult r = parser.parse("0109506000134352", config);
        assertEquals(1, r.getElements().size());
        assertTrue(r.getErrors().isEmpty());
        assertFalse(r.hasSyntaxErrors());
    }

    @Test
    @DisplayName("broken input: errors populated, hasSyntaxErrors true")
    void errorResult() {
        SyntaxParseResult r = parser.parse("XX", config);
        assertFalse(r.getErrors().isEmpty());
        assertTrue(r.hasSyntaxErrors());
    }

    @Test
    @DisplayName("element-string results carry no Digital Link info")
    void elementStringHasNoDigitalLink() {
        SyntaxParseResult r = parser.parse("0109506000134352", config);
        assertFalse(r.hasDigitalLink());
        assertNull(r.getDigitalLinkInfo());
    }

    @Test
    @DisplayName("Digital Link results carry the decomposed URL metadata")
    void digitalLinkResultCarriesInfo() {
        SyntaxParseResult r = new tools.pantheum.gaia.gs1.syntax.digitallink.DLSyntaxParser(
                tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry.getInstance())
                .parse("https://example.com/01/09506000134352", config);
        assertTrue(r.hasDigitalLink());
        assertEquals("example.com", r.getDigitalLinkInfo().getDomain());
        assertFalse(r.hasSyntaxErrors());
    }
}
