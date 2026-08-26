package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link AIDCMediaTypeValidator} — custom content validation for AI (7241). */
@DisplayName("AIDCMediaTypeValidator (AI 7241)")
class AIDCMediaTypeValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7241)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7241").orElse(null) instanceof AIDCMediaTypeValidator,
                "AI (7241) must be wired to AIDCMediaTypeValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7241) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("8017950600012345678907\u001D724101");
        assertTrue(resp.isValid(), "A valid AI (7241) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects code '00' is reserved with GE-C116")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("8017950600012345678907\u001D724100");
        assertFalse(resp.isValid(), "code '00' is reserved must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C116".equals(e.getId())),
                "The error must be GE-C116 (custom content validation)");
    }
}
