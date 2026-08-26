package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GIAIValidator} — custom content validation for AI (7023). */
@DisplayName("GIAIValidator (AI 7023)")
class GIAIValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7023)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7023").orElse(null) instanceof GIAIValidator,
                "AI (7023) must be wired to GIAIValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7023) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("70239506000123");
        assertTrue(resp.isValid(), "A valid AI (7023) element string must pass custom validation");
    }

    @Test
    @DisplayName("warns on unrecognised GS1 company prefix with GE-C132")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("7023A");
        assertTrue(resp.isValid(), "unrecognised GS1 company prefix is advisory (warning)");
        assertTrue(resp.getWarnings().stream().anyMatch(e -> "GE-C132".equals(e.getId())),
                "The warning must be GE-C132 (custom content validation)");
    }
}
