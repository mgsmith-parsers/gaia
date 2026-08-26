package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AISyntaxParser} — element string tokenisation. */
@DisplayName("AISyntaxParser")
class SyntaxParserTest {

    private final AISyntaxParser parser = new AISyntaxParser(AiDefinitionRegistry.getInstance());
    private final ParseConfig config = ParseConfig.defaultConfig();

    @Test
    @DisplayName("tokenises consecutive fixed-length AIs without separators")
    void fixedLengthTokenisation() {
        SyntaxParseResult r = parser.parse("010950600013435217261231", config);
        assertFalse(r.hasSyntaxErrors());
        assertEquals(2, r.getElements().size());
        assertEquals("01", r.getElements().get(0).getAi());
        assertEquals("17", r.getElements().get(1).getAi());
    }

    @Test
    @DisplayName("variable-length AI reads to FNC1")
    void variableLengthStopsAtFnc1() {
        SyntaxParseResult r = parser.parse("10LOT1\u001D17261231", config);
        assertFalse(r.hasSyntaxErrors());
        assertEquals("LOT1", r.getElements().get(0).getValue());
    }

    @Test
    @DisplayName("an unrecognisable AI code produces a syntax error")
    void unknownAiProducesError() {
        SyntaxParseResult r = parser.parse("XX123", config);
        assertTrue(r.hasSyntaxErrors());
    }
}
