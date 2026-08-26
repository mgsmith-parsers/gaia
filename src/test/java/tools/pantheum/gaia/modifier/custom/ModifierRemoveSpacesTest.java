package tools.pantheum.gaia.modifier.custom;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.modifier.ModifierInterface;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ModifierRemoveSpaces} — space removal, and the prefixes it must not touch. */
@DisplayName("ModifierRemoveSpaces")
class ModifierRemoveSpacesTest {

    private static final String GS = String.valueOf(GS1Constants.FNC1_GS);

    private final ModifierInterface modifier = new ModifierRemoveSpaces();

    @Test
    @DisplayName("spaces are removed from the element string")
    void removesSpaces() {
        assertEquals("010950600013435221SER123", modifier.modify("0109506000134352 21 SER 123"));
    }

    @Test
    @DisplayName("input with no spaces is returned unchanged")
    void noSpaces() {
        assertEquals("0109506000134352", modifier.modify("0109506000134352"));
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
        String once  = modifier.modify("0109506000134352 21 SER 123");
        String twice = modifier.modify(once);

        assertEquals(once, twice);
    }

    @Test
    @DisplayName("reports the display name rather than the class name")
    void reportsDisplayName() {
        assertEquals("Remove Space Characters", modifier.getName());
    }

    @Test
    @DisplayName("resolvable by class name through the registry")
    void resolvableByName() {
        ParseConfig config = ParseConfig.builder()
                .modifierClass(ModifierRemoveSpaces.class.getName())
                .build();

        assertEquals(1, config.getModifiers().size());
        assertInstanceOf(ModifierRemoveSpaces.class, config.getModifiers().get(0));
    }

    // -------------------------------------------------------------------------
    // What counts as a space
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("leading and trailing spaces are removed")
    void leadingAndTrailingSpaces() {
        assertEquals("0109506000134352", modifier.modify("  0109506000134352  "));
    }

    @Test
    @DisplayName("a run of consecutive spaces is removed in full")
    void consecutiveSpaces() {
        assertEquals("0109506000134352", modifier.modify("01     09506000134352"));
    }

    @Test
    @DisplayName("an all-space input collapses to empty")
    void onlySpaces() {
        assertEquals("", modifier.modify("   "));
    }

    @Test
    @DisplayName("only ASCII 0x20 is removed — a tab is left for the parser to reject")
    void onlySpaceCharacterIsRemoved() {
        // The GS1 encodable set excludes the tab, so removing it would mask a real
        // GE-S008 rather than repair a known upstream quirk.
        String in = "0109506000134352\t21SER123";
        assertEquals(in, modifier.modify(in));
    }

    @Test
    @DisplayName("the FNC1 separator survives space removal")
    void separatorIsPreserved() {
        String out = modifier.modify("010950600013435210LOT A" + GS + "17251231");

        assertEquals("010950600013435210LOTA" + GS + "17251231", out);
        assertEquals(1, out.chars().filter(c -> c == GS1Constants.FNC1_GS).count());
    }

    @Test
    @DisplayName("a space inside a value is removed too — the documented limitation")
    void spaceInsideAValueIsAlsoRemoved() {
        // 0x20 is part of the GS1 invariant set, so a batch/lot may legitimately contain
        // one. The modifier cannot tell a spurious space from a genuine one; this test
        // pins that trade-off rather than asserting it is desirable.
        assertEquals("010950600013435210LOTA1", modifier.modify("0109506000134352 10 LOT A1"));
    }

    // -------------------------------------------------------------------------
    // Prefixes: correlation ID, AIM Code ID, ECI — all optional, any combination
    // -------------------------------------------------------------------------

    /** The spaced payload, and the element string it becomes. */
    private static final String SPACED = "0109506000134352 21 SER123";
    private static final String BARE   = "010950600013435221SER123";

    @Test
    @DisplayName("correlation ID prefix is skipped and preserved")
    void correlationPrefix() {
        assertEquals("12345678~" + BARE, modifier.modify("12345678~" + SPACED));
    }

    @Test
    @DisplayName("AIM Code ID prefix is skipped and preserved")
    void aimCodeIdPrefix() {
        assertEquals("]C1" + BARE, modifier.modify("]C1" + SPACED));
    }

    @Test
    @DisplayName("AIM Code ID + ECI indicator are skipped and preserved")
    void aimCodeIdAndEciPrefix() {
        // ]d2 is ECI-capable; \000004 is a registered ECI indicator.
        assertEquals("]d2\\000004" + BARE, modifier.modify("]d2\\000004" + SPACED));
    }

    @Test
    @DisplayName("correlation + AIM Code ID + ECI together are skipped and preserved")
    void allThreePrefixes() {
        assertEquals("12345678~]d2\\000004" + BARE, modifier.modify("12345678~]d2\\000004" + SPACED));
    }

    @Test
    @DisplayName("an ECI-capable carrier with no ECI indicator is not over-skipped")
    void eciCapableCarrierWithoutIndicator() {
        assertEquals("]d2" + BARE, modifier.modify("]d2" + SPACED));
    }

    @Test
    @DisplayName("an unknown AIM Code ID is stepped over, and its payload still de-spaced")
    void unknownCarrierPrefix() {
        // The carrier is not in the registry, so nothing is known about the payload; the
        // parser will reject the carrier either way, and the prefix is left intact.
        assertEquals("]Z9" + BARE, modifier.modify("]Z9" + SPACED));
    }

    @Test
    @DisplayName("a space immediately after the AIM Code ID is removed, not treated as prefix")
    void spaceDirectlyAfterAimCodeId() {
        assertEquals("]C1" + BARE, modifier.modify("]C1 " + SPACED));
    }

    @Test
    @DisplayName("EAN/UPC carrier is skipped — its payload is a GTIN, not an AI element string")
    void eanUpcCarrierSkipped() {
        // ]E0 requires GTIN padding: there is no AI structure, and a spaced numeric value
        // is malformed input the parser should report rather than silently repair.
        String in = "]E0" + SPACED;
        assertEquals(in, modifier.modify(in));
    }

    // -------------------------------------------------------------------------
    // End to end
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("end to end — the parser resolves both AIs once the spaces are gone")
    void endToEnd() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveSpaces()).build();
        ParseResult r = new GaiaParser().parse(SPACED, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertEquals(BARE, r.getPayload());
        assertEquals(2, r.getAiObject().getAis().size());
        assertEquals("01", r.getAiObject().getAis().get(0).getAi());
        assertEquals("09506000134352", r.getAiObject().getAis().get(0).getValue());
        assertEquals("21", r.getAiObject().getAis().get(1).getAi());
        assertEquals("SER123", r.getAiObject().getAis().get(1).getValue());

        assertTrue(r.isInputModified());
        assertEquals(java.util.List.of("Remove Space Characters"),
                r.getModifierInfo().getAppliedModifiers());
        assertEquals(SPACED, r.getModifierInfo().getOriginalInput());
    }

    @Test
    @DisplayName("end to end — an input with no spaces is reported as unmodified")
    void endToEndNoRewrite() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveSpaces()).build();
        ParseResult r = new GaiaParser().parse(BARE, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertFalse(r.isInputModified());
        // The chain ran, so the info is present — it just lists no applied modifier.
        assertNotNull(r.getModifierInfo());
        assertTrue(r.getModifierInfo().getAppliedModifiers().isEmpty());
    }

    @Test
    @DisplayName("end to end — prefixes survive the parse and are still reported")
    void endToEndWithPrefixes() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveSpaces()).build();
        ParseResult r = new GaiaParser().parse("12345678~]C1" + SPACED, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertEquals(2, r.getAiObject().getAis().size());
        assertEquals("09506000134352", r.getAiObject().getAis().get(0).getValue());
        assertEquals("SER123", r.getAiObject().getAis().get(1).getValue());

        assertTrue(r.hasCorrelationId());
        assertEquals("12345678", r.getCorrelationInfo().getId());
        assertTrue(r.hasDataCarrier());
        assertEquals("]C1", r.getDataCarrier().getAimCodeId());
        assertTrue(r.isInputModified());
    }

    @Test
    @DisplayName("end to end — the ECI indicator is still decoded after space removal")
    void endToEndWithEci() {
        ParseConfig config = ParseConfig.builder().modifier(new ModifierRemoveSpaces()).build();
        ParseResult r = new GaiaParser().parse("]d2\\000004" + SPACED, config);

        assertTrue(r.isValid(), () -> "Errors: " + r.getErrors());
        assertTrue(r.hasEci());
        assertEquals("\\000004", r.getEci().getIndicator());
        assertEquals("]d2", r.getDataCarrier().getAimCodeId());
        assertEquals(2, r.getAiObject().getAis().size());
    }
}
