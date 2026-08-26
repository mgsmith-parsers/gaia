package tools.pantheum.gaia.gs1.content.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.result.ParseResult;

import java.util.List;

/** Tests for {@link NSNValidator} — custom content validation for AI (7001). */
@DisplayName("NSNValidator (AI 7001)")
class NSNValidatorTest {

    static final GaiaParser parser = new GaiaParser();

    /** AI (7001) requires one of (01)/(02)/(8006)/(8026), so every case carries a GTIN. */
    private static final String COMPANION = "0109506000134352";

    private static ParseResult parseNsn(String nsnValue) {
        return parser.parse(COMPANION + "7001" + nsnValue);
    }

    /** Asserts the value is rejected by content validation with exactly the given error. */
    private static void assertRejectedWith(String nsnValue, String errorId, String errorCode) {
        ParseResult resp = parseNsn(nsnValue);
        assertFalse(resp.isValid(), "NSN '" + nsnValue + "' must fail custom validation");

        List<GaiaError> errors = resp.getErrors();
        assertEquals(1, errors.size(),
                "NSNValidator returns on first failure, so exactly one error is expected: " + errors);

        GaiaError error = errors.get(0);
        assertEquals(errorId,   error.getId(),   "Unexpected error id for NSN '" + nsnValue + "'");
        assertEquals(errorCode, error.getCode(), "Unexpected error code for NSN '" + nsnValue + "'");
    }

    @Test
    @DisplayName("is registered for AI (7001)")
    void registeredForAi() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("7001").orElse(null) instanceof NSNValidator,
                "AI (7001) must be wired to NSNValidator");
    }

    @Test
    @DisplayName("accepts a valid AI (7001) value")
    void validValueAccepted() {
        ParseResult resp = parser.parse("010950600013435270011005000123456");
        assertTrue(resp.isValid(), "A valid AI (7001) element string must pass custom validation");
    }

    @Test
    @DisplayName("rejects an all-zeros NSN with GE-C154")
    void allZerosRejected() {
        assertRejectedWith("0000000000000", "GE-C154", "NSN_ALL_ZEROS");
    }

    @Test
    @DisplayName("rejects an NSCG of 0000 with GE-C155")
    void zeroNscgRejected() {
        assertRejectedWith("0000660123456", "GE-C155", "NSN_NSCG");
    }

    @Test
    @DisplayName("rejects an unassigned NCB code with GE-C156")
    void unknownNcbCodeRejected() {
        // NCB 11 is not assigned in the NSPA AC/135 list.
        assertRejectedWith("1005110123456", "GE-C156", "NSN_NCB_COUNTRY_CODE");
    }

    @Test
    @DisplayName("rejects an all-zeros item number with GE-C157")
    void allZerosItemNumberRejected() {
        assertRejectedWith("1005660000000", "GE-C157", "NSN_ITEM_ALL_ZEROS");
    }

    /** Builds an element directly, bypassing the parser, to reach the null paths it never produces. */
    private static GS1AIObjectElement elementOf(String elementValue, String componentValue) {
        AiDefinition def = AiDefinitionRegistry.getInstance().find("7001").orElseThrow();
        return new GS1AIObjectElement(def, elementValue, 0,
                List.of(new GS1AIComponentValue(def.getComponents().get(0), componentValue, 0)));
    }

    @Test
    @DisplayName("tolerates a null component value from a directly-built element")
    void nullComponentValueTolerated() {
        List<GaiaError> errors = assertDoesNotThrow(() ->
                NSNValidator.INSTANCE.validate(elementOf("7001x", null), GaiaConstants.Language.ENGLISH));
        assertTrue(errors.isEmpty(), "A null component value must be skipped, not reported");
    }

    @Test
    @DisplayName("tolerates a null element value when building the error parameters")
    void nullElementValueTolerated() {
        // Map.of rejects null values, so this reaches the error-construction path with a null.
        List<GaiaError> errors = assertDoesNotThrow(() ->
                NSNValidator.INSTANCE.validate(elementOf(null, "0000000000000"), GaiaConstants.Language.ENGLISH));
        assertEquals(1, errors.size(), "The all-zeros rule must still fire");
        assertEquals("GE-C154", errors.get(0).getId());
    }

    @Test
    @DisplayName("reports GE-C154 rather than GE-C155 for an all-zeros NSN")
    void allZerosTakesPrecedenceOverNscg() {
        // An all-zeros NSN also has an NSCG of 0000, so both rules match. The
        // all-zeros check runs first and must stay that way — see NSNValidator.
        ParseResult resp = parseNsn("0000000000000");
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C154".equals(e.getId())),
                "The more specific all-zeros error must win");
        assertFalse(resp.getErrors().stream().anyMatch(e -> "GE-C155".equals(e.getId())),
                "GE-C155 must not also be reported");
    }
}
