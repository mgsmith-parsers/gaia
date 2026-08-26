package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1Parser} — the GS1 content facade. */
@DisplayName("GS1Parser")
class GS1ParserTest {

    private final GS1Parser parser = new GS1Parser();

    @Test
    @DisplayName("routes element strings to the AI pipeline")
    void elementStringRouting() {
        GS1AIObject obj = parser.parse("0109506000134352");
        assertTrue(obj.isValid());
        assertEquals(1, obj.size());
        assertEquals("01", obj.getAis().get(0).getAi());
    }

    @Test
    @DisplayName("routes http(s) URIs to the Digital Link parser")
    void digitalLinkRouting() {
        GS1AIObject obj = parser.parse("https://example.com/01/09506000134352");
        assertNotNull(obj);
        assertEquals(1, obj.size(), "The Digital Link parser extracts the primary key");
        assertEquals("01", obj.getAis().get(0).getAi());
    }

    @Test
    @DisplayName("internalError wraps an exception as GE-I001")
    void internalErrorWrapsException() {
        GS1AIObject obj = GS1Parser.internalError(
                new IllegalStateException("boom"), ParseConfig.defaultConfig(), "GS1ParserTest");
        assertFalse(obj.isValid());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-I001".equals(e.getId())));
    }
}
