package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AICharacterSetChecker} — raw input character screening. */
@DisplayName("AICharacterSetChecker")
class CharacterSetCheckerTest {

    private final AICharacterSetChecker checker = new AICharacterSetChecker();
    private final ParseConfig config = ParseConfig.defaultConfig();

    @Test
    @DisplayName("clean GS1 input passes")
    void cleanInputPasses() {
        List<GaiaError> errors = checker.checkInput("0109506000134352\u001D10LOT1", config);
        assertTrue(errors.isEmpty(), "FNC1 and CSET 82 characters are allowed");
    }

    @Test
    @DisplayName("a non-encodable control character is reported")
    void controlCharacterReported() {
        List<GaiaError> errors = checker.checkInput("01\u0001", config);
        assertFalse(errors.isEmpty());
    }
}
