package tools.pantheum.gaia.gs1.interpretation.enricher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.result.ParseResult;

import java.util.List;

/** Tests for {@link NsnEnricher} — interpretation enrichment for AI (7001). */
@DisplayName("NsnEnricher (AI 7001)")
class NsnEnricherTest {

    static final GaiaParser parser = new GaiaParser();

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("NsnEnricher").isPresent(),
                "NsnEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("produces interpretations for a valid AI (7001) element")
    void producesInterpretations() {
        ParseResult resp = parser.parse("010950600013435270011005000123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");
        assertFalse(resp.getAiObject().get("7001").getInterpretations().isEmpty(),
                "AI (7001) must carry interpretations produced in INTERPRETATION mode");
    }

    @Test
    @DisplayName("carries the NCB country, CTR and category for a recognised code")
    void producesNcbFields() {
        // NSN 1005-66-012-3456 — NCB 66 is Australia (AUS, Tier 2).
        ParseResult resp = parser.parse("010950600013435270011005660123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("66",        valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_CODE));
        assertEquals("Australia", valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_NAME));
        assertEquals("AUS",       valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_CTR));
        assertEquals("TIER2",     valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_CAT));
    }

    @Test
    @DisplayName("reports the NIIN as the NCB code plus the item number")
    void niinIncludesNcbCode() {
        // NSN 1005-66-012-3456 — NIIN is the 9-digit tail 66 + 0123456.
        ParseResult resp = parser.parse("010950600013435270011005660123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("660123456", valueOf(interpretations, GS1Constants_Enricher.NSN_NIIN));
        assertEquals("1005-66-012-3456", valueOf(interpretations, GS1Constants_Enricher.NSN_FORMATTED),
                "The dash format must keep the NCB code as its own segment");
    }

    @Test
    @DisplayName("omits the CTR for NCB 44, which has no ISO country code")
    void omitsCtrForUnitedNations() {
        ParseResult resp = parser.parse("010950600013435270011005440123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("United Nations", valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_NAME));
        assertNull(valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_CTR),
                "NCB 44 carries no alpha-3 code, so no CTR interpretation may be emitted");
        assertEquals("OTHER", valueOf(interpretations, GS1Constants_Enricher.NSN_NCB_COUNTRY_CAT));
    }

    @Test
    @DisplayName("expands the FSC into its supply group and titles")
    void expandsFsc() {
        // FSC 1005 — group 10 (Weapons).
        ParseResult resp = parser.parse("010950600013435270011005660123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("1005",    valueOf(interpretations, GS1Constants_Enricher.NSN_FSCG));
        assertEquals("10",      valueOf(interpretations, GS1Constants_Enricher.NSN_FSG));
        assertEquals("Weapons", valueOf(interpretations, GS1Constants_Enricher.NSN_FSG_NAME));
        assertEquals("Weapons (from 1 mm through 30 mm)",
                valueOf(interpretations, GS1Constants_Enricher.NSN_FSCG_NAME));
    }

    @Test
    @DisplayName("omits the group name for an unassigned supply group")
    void omitsUnknownGroupName() {
        // FSG 21 is not an assigned Federal Supply Group.
        ParseResult resp = parser.parse("010950600013435270012105660123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("21", valueOf(interpretations, GS1Constants_Enricher.NSN_FSG));
        assertNull(valueOf(interpretations, GS1Constants_Enricher.NSN_FSG_NAME),
                "An unassigned FSG must not produce a group name");
        assertNull(valueOf(interpretations, GS1Constants_Enricher.NSN_FSCG_NAME),
                "An unassigned FSC must not produce a class name");
    }

    @Test
    @DisplayName("omits the class name for an unlisted class in a known group")
    void omitsUnknownClassName() {
        // FSG 10 (Weapons) is assigned, but 1002 is not one of its classes.
        ParseResult resp = parser.parse("010950600013435270011002660123456");
        assertTrue(resp.isValid(), "Input must be valid for the interpretation stage to run");

        List<GS1AIInterpretation> interpretations = resp.getAiObject().get("7001").getInterpretations();
        assertEquals("Weapons", valueOf(interpretations, GS1Constants_Enricher.NSN_FSG_NAME));
        assertNull(valueOf(interpretations, GS1Constants_Enricher.NSN_FSCG_NAME),
                "An unlisted FSC must not produce a class name even when its group is known");
    }

    private static String valueOf(List<GS1AIInterpretation> interpretations, String type) {
        return interpretations.stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst()
                .orElse(null);
    }
}
