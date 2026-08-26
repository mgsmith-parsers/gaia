package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link ProcessorValidator} — custom content validation for AI (7030). */
@DisplayName("ProcessorValidator (AI 7030)")
class ProcessorValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7030)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7030").orElse(null) instanceof ProcessorValidator,
                "AI (7030) must be wired to ProcessorValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7030) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("01095060001343527030036ACME");
        assertTrue(resp.isValid(), "A valid AI (7030) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects unknown ISO 3166 country '999' with GE-C158")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("01095060001343527030999ACME");
        assertFalse(resp.isValid(), "unknown ISO 3166 country '999' must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C158".equals(e.getId())),
                "The error must be GE-C158 (custom content validation)");
    }
}
