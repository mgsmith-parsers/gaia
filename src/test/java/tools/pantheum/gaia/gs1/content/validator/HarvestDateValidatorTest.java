package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link HarvestDateValidator} — custom content validation for AI (7007). */
@DisplayName("HarvestDateValidator (AI 7007)")
class HarvestDateValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (7007)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7007").orElse(null) instanceof HarvestDateValidator,
                "AI (7007) must be wired to HarvestDateValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7007) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("01095060001343527007261231");
        assertTrue(resp.isValid(), "A valid AI (7007) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects end date equal to start date with GE-C150")
    void invalidValueRejectedWithGeC007() {
        ParseResult resp = parser.parse("01095060001343527007261201261201");
        assertFalse(resp.isValid(), "end date equal to start date must fail custom validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C150".equals(e.getId())),
                "The error must be GE-C150 (custom content validation)");
    }
}
