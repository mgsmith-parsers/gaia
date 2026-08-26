package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.result.ParseResult;

/** Tests for {@link HIDRIValidator} — custom content validation for AI (8014). */
@DisplayName("HIDRIValidator (AI 8014)")
class HIDRIValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is registered for AI (8014)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("8014").orElse(null) instanceof HIDRIValidator,
                "AI (8014) must be wired to HIDRIValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (8014) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("010950600013435280149506000ABCAS");
        assertTrue(resp.isValid(), "A valid AI (8014) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects too short for a check character pair with GE-C004")
    void tooShortRejectedWithGeC004() {
        // The generic CheckDigitCharacterValidator fires first (checkCharacters attribute),
        // so a value too short for a valid pair reports GE-C004 and the custom
        // validator's GE-C147 minimum-length check is never reached.
        ParseResult resp = parser.parse("01095060001343528014AB");
        assertFalse(resp.isValid(), "too short for a check character pair must fail validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C004".equals(e.getId())),
                "The error must be GE-C004 (check character pair validation)");
    }

    @Test
    @DisplayName("rejects an incorrect check character pair with GE-C004")
    void invalidCheckPairRejectedWithGeC004() {
        ParseResult resp = parser.parse("010950600013435280149506000ABC22");
        assertFalse(resp.isValid(), "an incorrect check character pair must fail validation");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C004".equals(e.getId())),
                "The error must be GE-C004 (check character pair validation)");
    }
}
