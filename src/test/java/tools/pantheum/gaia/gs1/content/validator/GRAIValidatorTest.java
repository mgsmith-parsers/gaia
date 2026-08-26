package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GRAIValidator} — custom content validation for AI (8003). */
@DisplayName("GRAIValidator (AI 8003)")
class GRAIValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8003)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8003").orElse(null) instanceof GRAIValidator,
                "AI (8003) must be wired to GRAIValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8003) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("800309506000134352");
        assertTrue(resp.isValid(), "A valid AI (8003) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects all-zeros GRAI with GE-C139")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("800300000000000000");
        assertFalse(resp.isValid(), "all-zeros GRAI must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C139".equals(e.getId())),
                "The error must be GE-C139 (custom content validation)");
    }
}
