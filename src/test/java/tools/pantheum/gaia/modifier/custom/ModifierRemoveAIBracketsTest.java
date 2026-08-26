package tools.pantheum.gaia.modifier.custom;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.modifier.ModifierInterface;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ModifierRemoveAIBrackets} — HRI bracket removal and the separator it implies. */
@DisplayName("ModifierRemoveAIBrackets")
class ModifierRemoveAIBracketsTest {

    private static final String GS = String.valueOf(GS1Constants.FNC1_GS);

    private final ModifierInterface modifier = new ModifierRemoveAIBrackets();

    @Test
    @DisplayName("brackets are removed, leaving the bare AI codes")
    void removesBrackets() {
        assertEquals("010952123454321317251231", modifier.modify("(01)09521234543213(17)251231"));
    }

    @Test
    @DisplayName("input with no brackets is returned unchanged")
    void noBrackets() {
        assertEquals("0109521234543213", modifier.modify("0109521234543213"));
    }

    @Test
    @DisplayName("null and empty input are returned unchanged")
    void nullAndEmpty() {
        assertNull(modifier.modify(null));
        assertEquals("", modifier.modify(""));
    }

    @Test
    @DisplayName("applying the modifier twice is idempotent")
    void idempotent() {
        String once  = modifier.modify("(400)1234A1234567899(90)DD123");
        String twice = modifier.modify(once);

        assertEquals(once, twice);
    }

    @Test
    @DisplayName("reports the display name rather than the class name")
    void reportsDisplayName() {
        assertEquals("Remove Brackets Around AI", modifier.getName());
    }

    @Test
    @DisplayName("resolvable by class name through the registry")
    void resolvableByName() {
        ParseConfig config = ParseConfig.builder()
                .modifierClass(ModifierRemoveAIBrackets.class.getName())
                .build();

        assertEquals(1, config.getModifiers().size());
        assertInstanceOf(ModifierRemoveAIBrackets.class, config.getModifiers().get(0));
    }

    // -------------------------------------------------------------------------
    // The separator the brackets used to imply
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("a variable-length AI followed by another AI gets the separator the brackets implied")
    void insertsSeparatorAfterVariableLengthAi() {
        // AI (400) is N3+X..30 — separatorRequired. Without the FNC1 the bare element string
        // would be 4001234A123456789990DD123, where AI (400) swallows the AI (90) payload.
        assertEquals("4001234A1234567899" + GS + "90DD123",
                modifier.modify("(400)1234A1234567899(90)DD123"));
    }

    @Test
    @DisplayName("a fixed-length AI is not separated")
    void noSeparatorAfterFixedLengthAi() {
        // (01) is N14 and (3103) is N4+N6 — both fixed, so the tokeniser finds the boundary
        // by length and an inserted FNC1 would be data, not a delimiter.
        assertEquals("0109521234543213310300012310LOT1",
                modifier.modify("(01)09521234543213(3103)000123(10)LOT1"));
    }

    @Test
    @DisplayName("only the variable-length boundaries are separated, in a mixed string")
    void separatesOnlyVariableLengthBoundaries() {
        // (01) fixed → no separator; (10) variable → separator before (17).
        assertEquals("010952123454321310ABC123" + GS + "17251231",
                modifier.modify("(01)09521234543213(10)ABC123(17)251231"));
    }

    @Test
    @DisplayName("the trailing AI is not separated — its value ends at the end of the string")
    void noTrailingSeparator() {
        String out = modifier.modify("(01)09521234543213(10)ABC123");

        assertEquals("010952123454321310ABC123", out);
        assertFalse(out.endsWith(GS), "a trailing value needs no separator");
    }

    @Test
    @DisplayName("a single AI on its own is never separated")
    void singleAi() {
        assertEquals("10ABC123", modifier.modify("(10)ABC123"));
    }

    @Test
    @DisplayName("a value already ending in FNC1 does not get a second separator")
    void doesNotDoubleInsert() {
        String out = modifier.modify("(10)ABC123" + GS + "(21)SER1");

        assertEquals("10ABC123" + GS + "21SER1", out);
        assertEquals(1, out.chars().filter(c -> c == GS1Constants.FNC1_GS).count());
    }

    @Test
    @DisplayName("an unknown AI states nothing about its length — no separator is inserted")
    void unknownAiIsNotSeparated() {
        assertEquals("9999ABC90X", modifier.modify("(9999)ABC(90)X"));
    }

    @Test
    @DisplayName("text before the first bracketed AI belongs to no AI and is not separated")
    void textBeforeFirstBracketedAi() {
        // The owning AI of a leading unbracketed value is unknown, so no boundary is asserted.
        assertEquals("0109521234543213" + "10ABC123", modifier.modify("0109521234543213(10)ABC123"));
    }

    // -------------------------------------------------------------------------
    // Prefixes: correlation ID, AIM Code ID, ECI — all optional, any combination
    // -------------------------------------------------------------------------

    /** The bracketed HRI payload, and the element string it becomes. */
    private static final String HRI   = "(400)1234A1234567899(90)DD123";
    private static final String BARE  = "4001234A1234567899" + GS + "90DD123";

    @Test
    @DisplayName("correlation ID prefix is skipped and preserved")
    void correlationPrefix() {
        assertEquals("12345678~" + BARE, modifier.modify("12345678~" + HRI));
    }

    @Test
    @DisplayName("AIM Code ID prefix is skipped and preserved")
    void aimCodeIdPrefix() {
        assertEquals("]C1" + BARE, modifier.modify("]C1" + HRI));
    }

    @Test
    @DisplayName("AIM Code ID + ECI indicator are skipped and preserved")
    void aimCodeIdAndEciPrefix() {
        // ]d2 is ECI-capable; \000004 is a registered ECI indicator.
        assertEquals("]d2\\000004" + BARE, modifier.modify("]d2\\000004" + HRI));
    }

    @Test
    @DisplayName("correlation + AIM Code ID + ECI together are skipped and preserved")
    void allThreePrefixes() {
        assertEquals("12345678~]d2\\000004" + BARE, modifier.modify("12345678~]d2\\000004" + HRI));
    }

    @Test
    @DisplayName("an ECI-capable carrier with no ECI indicator is not over-skipped")
    void eciCapableCarrierWithoutIndicator() {
        assertEquals("]d2" + BARE, modifier.modify("]d2" + HRI));
    }

    @Test
    @DisplayName("EAN/UPC carrier is skipped — its payload is a GTIN, not an AI element string")
    void eanUpcCarrierSkipped() {
        // ]E0 requires GTIN padding: there is no AI structure to unbracket.
        String in = "]E0" + HRI;
        assertEquals(in, modifier.modify(in));
    }

    // -------------------------------------------------------------------------
    // End to end
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("end to end — the parser resolves both AIs once the brackets become a separator")
    void endToEnd() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveAIBrackets()).build();
        ParseResult r = new GaiaParser().parse(HRI, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertEquals(2, r.getAiObject().getAis().size());
        assertEquals("400", r.getAiObject().getAis().get(0).getAi());
        assertEquals("1234A1234567899", r.getAiObject().getAis().get(0).getValue());
        assertEquals("90", r.getAiObject().getAis().get(1).getAi());
        assertEquals("DD123", r.getAiObject().getAis().get(1).getValue());

        assertTrue(r.isInputModified());
        assertEquals(java.util.List.of("Remove Brackets Around AI"),
                r.getModifierInfo().getAppliedModifiers());
    }

    @Test
    @DisplayName("end to end — a fixed-length AI keeps parsing without a separator")
    void endToEndFixedLength() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveAIBrackets()).build();
        ParseResult r = new GaiaParser().parse("(01)09521234543213(17)251231(10)ABC123", config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertEquals(3, r.getAiObject().getAis().size());
        assertEquals("09521234543213", r.getAiObject().getAis().get(0).getValue());
        assertEquals("251231", r.getAiObject().getAis().get(1).getValue());
        assertEquals("ABC123", r.getAiObject().getAis().get(2).getValue());
    }

    @Test
    @DisplayName("end to end — prefixes survive the parse and are still reported")
    void endToEndWithPrefixes() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveAIBrackets()).build();
        ParseResult r = new GaiaParser().parse("12345678~]C1" + HRI, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertEquals(2, r.getAiObject().getAis().size());
        assertEquals("1234A1234567899", r.getAiObject().getAis().get(0).getValue());
        assertEquals("DD123", r.getAiObject().getAis().get(1).getValue());

        assertTrue(r.hasCorrelationId());
        assertEquals("12345678", r.getCorrelationInfo().getId());
        assertTrue(r.hasDataCarrier());
        assertEquals("]C1", r.getDataCarrier().getAimCodeId());
        assertTrue(r.isInputModified());
    }
}
