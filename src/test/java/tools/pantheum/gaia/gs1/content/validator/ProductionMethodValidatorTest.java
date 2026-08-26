package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link ProductionMethodValidator} — custom content validation for AI (7010). */
@DisplayName("ProductionMethodValidator (AI 7010)")
class ProductionMethodValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7010)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7010").orElse(null) instanceof ProductionMethodValidator,
                "AI (7010) must be wired to ProductionMethodValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7010) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("010950600013435270100");
        assertTrue(resp.isValid(), "A valid AI (7010) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects unknown production method code with GE-C163")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("01095060001343527010Z");
        assertFalse(resp.isValid(), "unknown production method code must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C163".equals(e.getId())),
                "The error must be GE-C163 (custom content validation)");
    }
}
