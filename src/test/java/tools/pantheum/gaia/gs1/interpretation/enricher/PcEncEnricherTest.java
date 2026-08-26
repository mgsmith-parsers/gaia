package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.result.ParseResult;

import java.util.List;

/** Tests for {@link PcEncEnricher} — percent-decoding of {@code pcenc} AI values. */
@DisplayName("PcEncEnricher (pcenc AIs)")
class PcEncEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    /** A fully valid AI (7253) element string carrying {@code value}. */
    private static String input7253(String value) {
        return GS1Constants_AICodes.AI_8017_GSRN_PROVIDER + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7253_FAMILY_NAME + value;
    }

    /** The DECODED_TEXT interpretation produced for {@code ai}, or null if there is none. */
    private static String decodedText(ParseResult resp, String ai) {
        return resp.getAiObject().get(ai).getInterpretations().stream()
                .filter(i -> GS1Constants_Enricher.DECODED_TEXT.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst().orElse(null);
    }

    /** Runs the enricher against the registry definition for {@code ai}. */
    private static String enrich(String ai, String value) {
        AiDefinition def = AiDefinitionRegistry.getInstance().find(ai)
                .orElseThrow(() -> new AssertionError("AI (" + ai + ") must be in the registry"));
        List<GS1AIInterpretation> out = new PcEncEnricher().enrich(value, def, null);
        assertEquals(1, out.size(), "AI (" + ai + ") must produce exactly one interpretation");
        return out.get(0).getValue();
    }

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("PcEncEnricher").isPresent(),
                "PcEncEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (4300) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("000950600013435211134300A");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("4300").getInterpretations().isEmpty(),
                "AI (4300) must carry interpretations produced in INTERPRETATION mode");
    }

    @Test
    @DisplayName("decode percent-decodes UTF-8 and maps a plus to a space")
    void decodeUtf8AndPlus() {
        assertEquals("François", PcEncEnricher.decode("Fran%C3%A7ois"));
        assertEquals("A B", PcEncEnricher.decode("A+B"));
        assertEquals("O Brien", PcEncEnricher.decode("O+Brien"));
    }

    @Test
    @DisplayName("decode returns a malformed value unchanged (lenient)")
    void decodeMalformedReturnedRaw() {
        assertEquals("bad%2", PcEncEnricher.decode("bad%2"));
    }

    @Test
    @DisplayName("a space is encoded as a single plus for every pcenc AI")
    void plusIsSpaceForAllPcencAis() {
        for (String ai : List.of("4300", "4302", "4306", "4310", "4316", "4320",
                                 GS1Constants_AICodes.AI_7253_FAMILY_NAME,
                                 GS1Constants_AICodes.AI_7254_GIVEN_NAME,
                                 GS1Constants_AICodes.AI_7256_FULL_NAME,
                                 GS1Constants_AICodes.AI_7257_PERSON_ADDR,
                                 GS1Constants_AICodes.AI_7259_BABY)) {
            assertEquals("Mary Jane", enrich(ai, "Mary+Jane"),
                    "AI (" + ai + ") must decode a single plus as a space");
            // The plus-as-space convention is applied after RFC 3986 decoding, so a %2B
            // (which decodes to '+') also becomes a space — a literal '+' is not
            // representable in these fields.
            assertEquals("A B", enrich(ai, "A%2BB"),
                    "AI (" + ai + ") must map %2B to a space (post-decode plus-as-space)");
        }
    }

    @Test
    @DisplayName("plus stays a literal plus for non-pcenc AIs")
    void plusIsLiteralForNonPcencAis() {
        // 7255 (SUFFIX) is a plain X..10, not pcenc, so it is not wired to this enricher
        // and the plus-as-space rule must not extend to it.
        ParseResult resp = parser.parse(GS1Constants_AICodes.AI_8017_GSRN_PROVIDER + "950600012345678907"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_7255_SUFFIX + "A+B");
        assertTrue(resp.isValid(), "A plus in AI (7255) must be valid");
        assertEquals("A+B", resp.getAiObject().get(GS1Constants_AICodes.AI_7255_SUFFIX).getValue(),
                "AI (7255) must preserve a literal plus sign");
        assertNull(decodedText(resp, GS1Constants_AICodes.AI_7255_SUFFIX),
                "AI (7255) is not pcenc, so it must carry no DECODED_TEXT interpretation");
    }

    @Test
    @DisplayName("AI (7253) percent-decoding reaches the interpretation through the parser")
    void decodedTextThroughParser() {
        ParseResult resp = parser.parse(input7253("Fran%C3%A7ois"));
        assertTrue(resp.isValid(), "A percent-encoded AI (7253) value must be valid");
        assertEquals("François", decodedText(resp, GS1Constants_AICodes.AI_7253_FAMILY_NAME),
                "AI (7253) must carry the percent-decoded value as DECODED_TEXT");

        resp = parser.parse(input7253("O%2BBrien"));
        assertTrue(resp.isValid(), "An escaped plus in AI (7253) must be valid");
        assertEquals("O Brien", decodedText(resp, GS1Constants_AICodes.AI_7253_FAMILY_NAME),
                "%2B decodes to '+', which the post-decode plus-as-space rule maps to a space");
    }

    @Test
    @DisplayName("a bare plus in AI (7253) is accepted and decoded as a space")
    void barePlusDecodesAsSpace() {
        // GS1 encodes a space in AI (7253) as a single '+'. PcencValidator accepts a bare '+',
        // so the value reaches the enricher, where the post-decode plus-as-space rule maps it
        // to a space — "O+Brien" decodes to "O Brien".
        ParseResult resp = parser.parse(input7253("O+Brien"));
        assertTrue(resp.isValid(), "A bare plus must be accepted by pcenc content validation");
        assertEquals("O Brien", decodedText(resp, GS1Constants_AICodes.AI_7253_FAMILY_NAME),
                "A bare '+' must decode to a space via the plus-as-space rule");
    }
}
