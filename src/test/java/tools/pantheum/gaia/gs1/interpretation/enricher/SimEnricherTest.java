package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.Iso7812Data;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EidEnricher} (AI 8042 — ESIM) and {@link IccidEnricher}
 * (AI 8043 — PSIM).
 *
 * <p>Both AIs require (01)+(21)+(8040), so every input prepends those.
 */
@DisplayName("EidEnricher / IccidEnricher (AI 8042 / 8043)")
class SimEnricherTest {

    /** A Luhn-valid EID: MII 89, check digit 0. */
    private static final String VALID_EID = "89044030050088826003380898765430";

    static GaiaParser parser;

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    /** Parses a full element string carrying {@code value} on {@code ai} and returns that element. */
    private static GS1AIObjectElement parseWith(String ai, String value) {
        ParseResult resp = parser.parse(
                GS1Constants_AICodes.AI_01_GTIN + "09506000134352"
                + GS1Constants_AICodes.AI_21_SERIAL + "A"
                + GS1Constants.FNC1_GS
                + GS1Constants_AICodes.AI_8040_IMEI + "490154203237518"
                + GS1Constants.FNC1_GS
                + ai + value);
        assertTrue(resp.isValid(), () -> "Input must be valid for interpretation: " + resp.getErrors());
        return resp.getAiObject().get(ai);
    }

    @Test
    @DisplayName("both enrichers are resolvable from the interpretation registry")
    void resolvableFromRegistry() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("EidEnricher").isPresent(),
                "EidEnricher must be resolvable by simple class name");
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("IccidEnricher").isPresent(),
                "IccidEnricher must be resolvable by simple class name");
    }

    @Test
    @DisplayName("EID decomposes into MII, body and check digit")
    void eidDecomposes() {
        GS1AIObjectElement e = parseWith(GS1Constants_AICodes.AI_8042_ESIM, VALID_EID);
        assertEquals("89", e.getInterpretation(GS1Constants_Enricher.SIM_MII).getValue(),
                "MII is the first 2 digits");
        assertEquals(VALID_EID.substring(2, 31),
                e.getInterpretation(GS1Constants_Enricher.EID_BODY).getValue(),
                "Body is digits 3–31");
        assertEquals("0", e.getInterpretation(GS1Constants_Enricher.EID_CHECK_DIGIT).getValue(),
                "Check digit is the final digit");
    }

    @Test
    @DisplayName("ICCID without the optional extension yields MII and body only")
    void iccidWithoutExtension() {
        GS1AIObjectElement e = parseWith(GS1Constants_AICodes.AI_8043_PSIM, "894400000000000010");
        assertEquals("89", e.getInterpretation(GS1Constants_Enricher.SIM_MII).getValue());
        assertEquals("4400000000000010", e.getInterpretation(GS1Constants_Enricher.ICCID_BODY).getValue());
        assertNull(e.getInterpretation(GS1Constants_Enricher.ICCID_EXTENSION),
                "No extension interpretation when the optional component is absent");
    }

    @Test
    @DisplayName("ICCID with the optional extension surfaces it separately")
    void iccidWithExtension() {
        GS1AIObjectElement e = parseWith(GS1Constants_AICodes.AI_8043_PSIM, "89440000000000001099");
        assertEquals("4400000000000010", e.getInterpretation(GS1Constants_Enricher.ICCID_BODY).getValue(),
                "Body is the 18-digit first component, less the MII");
        assertEquals("99", e.getInterpretation(GS1Constants_Enricher.ICCID_EXTENSION).getValue(),
                "Extension is the optional second component");
    }

    @Test
    @DisplayName("EID names the ISO/IEC 7812 industry category, last in the list")
    void eidMiiName() {
        GS1AIObjectElement e = parseWith(GS1Constants_AICodes.AI_8042_ESIM, VALID_EID);

        GS1AIInterpretation name = e.getInterpretation(GS1Constants_Enricher.SIM_MII_NAME);
        assertNotNull(name, "MII 89 resolves via its leading digit");
        assertEquals("Healthcare, telecommunications and other future industry assignments",
                name.getValue());
        assertEquals("Industry category", name.getLabel());

        List<GS1AIInterpretation> all = e.getInterpretations();
        assertEquals(GS1Constants_Enricher.SIM_MII_NAME, all.get(all.size() - 1).getType(),
                "the industry category is appended last");
    }

    @Test
    @DisplayName("the category is resolved from the MII's leading digit, not the pair")
    void miiIsTheLeadingDigit() {
        // ISO/IEC 7812 defines the MII as one digit; "89" is the E.118 telecom pair.
        assertEquals(Iso7812Data.nameForCode("8"), Iso7812Data.nameForIdentifier("89"),
                "89 must resolve exactly as its leading digit 8 does");
        assertEquals("Airlines", Iso7812Data.nameForIdentifier("12").orElseThrow(),
                "a different leading digit gives a different category");
        assertTrue(Iso7812Data.nameForCode("89").isEmpty(),
                "nameForCode takes a single digit only");
    }

    @Test
    @DisplayName("every ISO/IEC 7812 digit 0-9 is catalogued")
    void allDigitsCatalogued() {
        for (int d = 0; d <= 9; d++) {
            assertTrue(Iso7812Data.nameForCode(String.valueOf(d)).isPresent(),
                    "MII digit " + d + " must be catalogued");
        }
        assertEquals(10, Iso7812Data.INDUSTRY_CATEGORIES.size());
        assertTrue(Iso7812Data.nameForIdentifier("").isEmpty(), "empty input yields no category");
        assertTrue(Iso7812Data.nameForIdentifier(null).isEmpty(), "null input yields no category");
    }
}
