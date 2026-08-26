package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link GSRNValidator} — custom content validation for AI (8017). */
@DisplayName("GSRNValidator (AI 8017)")
class GSRNValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8017)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8017").orElse(null) instanceof GSRNValidator,
                "AI (8017) must be wired to GSRNValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8017) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("8017950600012345678907");
        assertTrue(resp.isValid(), "A valid AI (8017) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects all-zeros GSRN with GE-C143")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("8017000000000000000000");
        assertFalse(resp.isValid(), "all-zeros GSRN must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C143".equals(e.getId())),
                "The error must be GE-C143 (custom content validation)");
    }
}
