package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link AquaticSpeciesValidator} — custom content validation for AI (7008). */
@DisplayName("AquaticSpeciesValidator (AI 7008)")
class AquaticSpeciesValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7008)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7008").orElse(null) instanceof AquaticSpeciesValidator,
                "AI (7008) must be wired to AquaticSpeciesValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7008) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("01095060001343527008GXM");
        assertTrue(resp.isValid(), "A valid AI (7008) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects unknown ASFIS species code with GE-C121")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("01095060001343527008ZZZ");
        assertFalse(resp.isValid(), "unknown ASFIS species code must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C121".equals(e.getId())),
                "The error must be GE-C121 (custom content validation)");
    }
}
