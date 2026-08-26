package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1AIParser} — pipeline depth per {@link GS1Constants.ParseMode}. */
@DisplayName("GS1AIParser")
class GS1AIParserTest {

    private final GS1AIParser parser = new GS1AIParser();
    private final ParseConfig config = ParseConfig.defaultConfig();

    /** GTIN with an incorrect check digit — tokenises fine, fails content validation. */
    private static final String BAD_CD = "0109506000134353";
    private static final String GOOD   = "0109506000134352";

    @Test
    @DisplayName("SYNTAX mode skips content validation")
    void syntaxModeSkipsContent() {
        GS1AIObject obj = parser.parse(BAD_CD, GS1Constants.ParseMode.SYNTAX, config);
        assertTrue(obj.isValid(), "A check digit error must not be detected in SYNTAX mode");
    }

    @Test
    @DisplayName("CONTENT mode detects the check digit error")
    void contentModeValidates() {
        GS1AIObject obj = parser.parse(BAD_CD, GS1Constants.ParseMode.CONTENT, config);
        assertFalse(obj.isValid());
        assertTrue(obj.getAllErrors().stream().anyMatch(e -> "GE-C003".equals(e.getId())));
    }

    @Test
    @DisplayName("CONTENT mode does not enrich")
    void contentModeDoesNotEnrich() {
        GS1AIObject obj = parser.parse(GOOD, GS1Constants.ParseMode.CONTENT, config);
        assertTrue(obj.isValid());
        assertTrue(obj.getAis().get(0).getInterpretations().isEmpty(),
                "Interpretations are only produced in INTERPRETATION mode");
    }

    @Test
    @DisplayName("INTERPRETATION mode enriches valid elements")
    void interpretationModeEnriches() {
        GS1AIObject obj = parser.parse(GOOD, GS1Constants.ParseMode.INTERPRETATION, config);
        assertTrue(obj.isValid());
        assertFalse(obj.getAis().get(0).getInterpretations().isEmpty());
    }

    @Test
    @DisplayName("INTERPRETATION mode skips enrichment when content errors exist")
    void interpretationSkippedOnContentErrors() {
        GS1AIObject obj = parser.parse(BAD_CD, GS1Constants.ParseMode.INTERPRETATION, config);
        assertFalse(obj.isValid());
        assertTrue(obj.getAis().get(0).getInterpretations().isEmpty(),
                "Enrichment must not run when an element carries an error");
    }
}
