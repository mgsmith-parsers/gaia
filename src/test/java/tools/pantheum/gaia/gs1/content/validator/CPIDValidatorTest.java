package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link CPIDValidator} — custom content validation for AI (8010). */
@DisplayName("CPIDValidator (AI 8010)")
class CPIDValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8010)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8010").orElse(null) instanceof CPIDValidator,
                "AI (8010) must be wired to CPIDValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8010) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("80109506000ABC");
        assertTrue(resp.isValid(), "A valid AI (8010) element string must pass custom validation");
    }

    @Test
    @DisplayName("warns on unrecognised CPID prefix with GE-C122")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("8010#####");
        assertTrue(resp.isValid(), "unrecognised CPID prefix is advisory (warning)");
        assertTrue(resp.getWarnings().stream().anyMatch(e -> "GE-C122".equals(e.getId())),
                "The warning must be GE-C122 (custom content validation)");
    }
}
