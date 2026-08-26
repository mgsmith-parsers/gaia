package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link ProductUrlValidator} — custom content validation for AI (8200). */
@DisplayName("ProductUrlValidator (AI 8200)")
class ProductUrlValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8200)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8200").orElse(null) instanceof ProductUrlValidator,
                "AI (8200) must be wired to ProductUrlValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8200) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("01095060001343528200http://example.com/p");
        assertTrue(resp.isValid(), "A valid AI (8200) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects non-http(s) scheme with GE-C161")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("01095060001343528200ftp://x.com");
        assertFalse(resp.isValid(), "non-http(s) scheme must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C161".equals(e.getId())),
                "The error must be GE-C161 (custom content validation)");
    }
}
