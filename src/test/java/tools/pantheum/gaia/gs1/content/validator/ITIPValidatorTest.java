package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link ITIPValidator} — custom content validation for AI (8006). */
@DisplayName("ITIPValidator (AI 8006)")
class ITIPValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8006)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8006").orElse(null) instanceof ITIPValidator,
                "AI (8006) must be wired to ITIPValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8006) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("8006095060001343520101");
        assertTrue(resp.isValid(), "A valid AI (8006) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects all-zeros GTIN with GE-C152")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("8006000000000000000101");
        assertFalse(resp.isValid(), "all-zeros GTIN must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C152".equals(e.getId())),
                "The error must be GE-C152 (custom content validation)");
    }
}
