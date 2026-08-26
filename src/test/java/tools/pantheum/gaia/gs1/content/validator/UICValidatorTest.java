package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link UICValidator} — custom content validation for AI (7040). */
@DisplayName("UICValidator (AI 7040)")
class UICValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7040)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7040").orElse(null) instanceof UICValidator,
                "AI (7040) must be wired to UICValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7040) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("70401AAA");
        assertTrue(resp.isValid(), "A valid AI (7040) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects non-invariant UIC character with GE-C166")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("70401!AA");
        assertFalse(resp.isValid(), "non-invariant UIC character must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C166".equals(e.getId())),
                "The error must be GE-C166 (custom content validation)");
    }
}
