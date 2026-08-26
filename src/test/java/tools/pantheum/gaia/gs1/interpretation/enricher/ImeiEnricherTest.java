package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.ImeiRbiData;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ImeiEnricher} — TAC / serial / check-digit decomposition of
 * AI 8040 (IMEI) and AI 8041 (IMEI2).
 */
@DisplayName("ImeiEnricher (AI 8040 / 8041)")
class ImeiEnricherTest {

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** Parses a full element string carrying {@code imei} on AI (8040) and returns that element. */
    private static GS1AIObjectElement parse8040(String imei) {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + imei);
        assertTrue(resp.isValid(), () -> "Input must be valid for interpretation: " + resp.getErrors());
        return resp.getAiObject().get(GS1Constants_AICodes.AI_8040_IMEI);
    }

    @Test
    @DisplayName("is resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("ImeiEnricher").isPresent(),
                "ImeiEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("decomposes an IMEI into TAC, serial and check digit")
    void decomposes() {
        GS1AIObjectElement e = parse8040("490154203237518");
        assertEquals("49015420", e.getInterpretation(GS1Constants_Enricher.IMEI_TAC).getValue(),
                "TAC is the first 8 digits");
        assertEquals("323751", e.getInterpretation(GS1Constants_Enricher.IMEI_SERIAL).getValue(),
                "Serial is digits 9–14");
        assertEquals("8", e.getInterpretation(GS1Constants_Enricher.IMEI_CHECK_DIGIT).getValue(),
                "Check digit is the final digit");
    }

    @Test
    @DisplayName("reports the Reporting Body Identifier leading the TAC")
    void reportingBodyIdentifier() {
        GS1AIObjectElement e = parse8040("490154203237518");
        assertEquals("49", e.getInterpretation(GS1Constants_Enricher.IMEI_RBI).getValue(),
                "RBI is the first 2 digits of the TAC");
    }

    @Test
    @DisplayName("formats the IMEI as AA-BBBBBB-CCCCCC-D")
    void formats() {
        GS1AIObjectElement e = parse8040("490154203237518");
        assertEquals("49-015420-323751-8",
                e.getInterpretation(GS1Constants_Enricher.IMEI_FORMATTED).getValue(),
                "Standard GSMA display grouping is 2-6-6-1");
    }

    @Test
    @DisplayName("a leading-zero RBI keeps both digits")
    void leadingZeroRbi() {
        // TAC 01154200, serial 132000, Luhn check digit 9.
        GS1AIObjectElement e = parse8040("011542001320009");
        assertEquals("01", e.getInterpretation(GS1Constants_Enricher.IMEI_RBI).getValue(),
                "RBI is a 2-digit string, not a number");
        assertEquals("01-154200-132000-9",
                e.getInterpretation(GS1Constants_Enricher.IMEI_FORMATTED).getValue());
    }

    @Test
    @DisplayName("the new interpretations carry localized labels")
    void labels() {
        GS1AIObjectElement e = parse8040("490154203237518");
        assertEquals("IMEI", e.getInterpretation(GS1Constants_Enricher.IMEI_FORMATTED).getLabel());
        assertEquals("Reporting Body Identifier (RBI)",
                e.getInterpretation(GS1Constants_Enricher.IMEI_RBI).getLabel());
    }

    @Test
    @DisplayName("names the reporting body for a listed RBI, last in the list")
    void reportingBodyName() {
        // TAC 35154200, serial 132000; RBI 35 is TÜV SÜD BABT. Luhn check digit 7.
        GS1AIObjectElement e = parse8040("351542001320007");

        GS1AIInterpretation name = e.getInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME);
        assertNotNull(name, "RBI 35 is a listed reporting body");
        assertEquals("TÜV SÜD BABT (United Kingdom)", name.getValue());
        assertEquals("Reporting body", name.getLabel());

        List<GS1AIInterpretation> all = e.getInterpretations();
        assertEquals(GS1Constants_Enricher.IMEI_RBI_NAME, all.get(all.size() - 1).getType(),
                "the reporting-body name is appended last");
    }

    @Test
    @DisplayName("names a body that no longer allocates IMEIs")
    void retiredReportingBody() {
        // RBI 49 (BZT / BAPT, Germany) stopped allocating; devices carrying it are ordinary.
        GS1AIObjectElement e = parse8040("490154203237518");
        assertEquals("BZT / BAPT (Germany)",
                e.getInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME).getValue());
        assertTrue(ImeiRbiData.isNoLongerAllocating("49"));
        assertFalse(ImeiRbiData.isNoLongerAllocating("35"), "35 still allocates");
    }

    @Test
    @DisplayName("names the test-IMEI ranges")
    void testRanges() {
        assertEquals("Test IMEI (2-digit country codes)",
                parse8040("001542001320001")
                        .getInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME).getValue());
        assertEquals("Test IMEI (3-digit country codes)",
                parse8040("031542001320005")
                        .getInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME).getValue());

        assertTrue(ImeiRbiData.isTestCode("00"));
        assertTrue(ImeiRbiData.isTestCode("09"));
        assertFalse(ImeiRbiData.isTestCode("01"), "01 is CTIA / PTCRB, not a test range");
    }

    @Test
    @DisplayName("omits the reporting body for an RBI absent from the table")
    void unlistedReportingBody() {
        // RBI 60 is unallocated in ImeiRbiData; the IMEI is still perfectly valid.
        GS1AIObjectElement e = parse8040("601542001320005");
        assertEquals("60", e.getInterpretation(GS1Constants_Enricher.IMEI_RBI).getValue());
        assertNull(e.getInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME),
                "an unlisted RBI yields no name interpretation");
        assertEquals(5, e.getInterpretations().size(),
                "the other five interpretations are unaffected");
    }

    @Test
    @DisplayName("an unlisted RBI is not a validation failure")
    void unlistedRbiIsNotAnError() {
        // AI 8040 requires 01 AND 21, so the IMEI is carried in a full element string.
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "601542001320005");
        assertTrue(resp.isValid(),
                () -> "an RBI absent from the table must not affect validity: " + resp.getErrors());
        assertTrue(resp.getErrors().isEmpty(), "no error is raised for an unlisted RBI");
    }

    @Test
    @DisplayName("every catalogued RBI is a distinct 2-digit code")
    void tableWellFormed() {
        for (String code : ImeiRbiData.REPORTING_BODIES.keySet()) {
            assertTrue(code.matches("\\d{2}"), () -> "RBI must be 2 digits: " + code);
        }
        assertTrue(ImeiRbiData.REPORTING_BODIES.keySet()
                        .containsAll(ImeiRbiData.NO_LONGER_ALLOCATING),
                "every retired code must also carry a name");
        assertTrue(ImeiRbiData.REPORTING_BODIES.keySet().containsAll(ImeiRbiData.TEST_CODES),
                "every test code must also carry a name");
        assertTrue(Collections.disjoint(ImeiRbiData.TEST_CODES, ImeiRbiData.NO_LONGER_ALLOCATING),
                "a code cannot be both a test range and a retired body");
    }
}
