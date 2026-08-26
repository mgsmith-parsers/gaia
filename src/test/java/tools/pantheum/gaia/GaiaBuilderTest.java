package tools.pantheum.gaia;

import tools.pantheum.gaia.result.BuildResult;
import tools.pantheum.gaia.config.BuilderDigitalLinkConfig;
import tools.pantheum.gaia.error.GaiaBuilderException;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GaiaBuilder")
class GaiaBuilderTest {

    private static final GaiaParser PARSER = new GaiaParser();

    // -------------------------------------------------------------------------
    // Element string
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Element string: GTIN + batch + expiry round-trips through the parser")
    void elementStringRoundTrip() {
        String es = GaiaBuilder.create()
                .ai("01", "09506000134352")
                .ai("10", "LOT-ABC")
                .ai("17", "271231")
                .buildElementString();

        ParseResult resp = PARSER.parse(es);
        assertTrue(resp.isValid(), () -> "built element string should re-parse cleanly: " + resp.getErrors());
        assertTrue(resp.getAiObject().contains("01"));
        assertTrue(resp.getAiObject().contains("10"));
        assertTrue(resp.getAiObject().contains("17"));
    }

    @Test
    @DisplayName("Element string: single GTIN")
    void elementStringSingleGtin() {
        assertEquals("0109506000134352",
                GaiaBuilder.create().ai("01", "09506000134352").buildElementString());
    }

    @Test
    @DisplayName("Element string: invalid GTIN check digit throws")
    void elementStringInvalidCheckDigitThrows() {
        GaiaBuilderException ex = assertThrows(GaiaBuilderException.class,
                () -> GaiaBuilder.create().ai("01", "09506000134350").buildElementString());
        assertFalse(ex.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Element string: a batch/lot alone (no required primary) throws")
    void elementStringMissingRequiredThrows() {
        assertThrows(GaiaBuilderException.class,
                () -> GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString());
    }

    @Test
    @DisplayName("Element string: no AIs supplied throws")
    void elementStringEmptyThrows() {
        assertThrows(GaiaBuilderException.class, () -> GaiaBuilder.create().buildElementString());
    }

    // -------------------------------------------------------------------------
    // Digital Link URI
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Digital Link: canonical URI with primary, qualifier, and data attribute")
    void digitalLinkCanonical() {
        String dl = GaiaBuilder.create()
                .ai("01", "09506000134352")
                .ai("10", "LOT-ABC")
                .ai("17", "271231")
                .buildDigitalLinkUri();
        assertEquals("https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231", dl);

        // and it round-trips back to a valid Digital Link
        ParseResult resp = PARSER.parse(dl);
        assertTrue(resp.isValid());
        assertTrue(resp.getAiObject().hasDigitalLink());
    }

    @Test
    @DisplayName("Digital Link: single GTIN")
    void digitalLinkSingleGtin() {
        assertEquals("https://id.gs1.org/01/09506000134352",
                GaiaBuilder.create().ai("01", "09506000134352").buildDigitalLinkUri());
    }

    @Test
    @DisplayName("Digital Link: config sets base URL, extra query param, and fragment")
    void digitalLinkWithConfig() {
        BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder()
                .baseUrl("https://example.com/resolver")
                .addQueryParam("context", "retail")
                .fragment("section-2")
                .build();
        String dl = GaiaBuilder.create().ai("01", "09506000134352").buildDigitalLinkUri(cfg);
        assertEquals("https://example.com/resolver/01/09506000134352?context=retail#section-2", dl);
    }

    @Test
    @DisplayName("Digital Link: a value needing percent-encoding round-trips (build-time verification passes)")
    void digitalLinkPercentEncodedRoundTrips() {
        String dl = GaiaBuilder.create()
                .ai("01", "09506000134352")
                .ai("10", "AB/CD")            // '/' must be percent-encoded in the path
                .buildDigitalLinkUri();
        assertEquals("https://id.gs1.org/01/09506000134352/10/AB%2FCD", dl);

        // building it at all means the internal round-trip verification passed; confirm independently
        ParseResult resp = PARSER.parse(dl);
        assertTrue(resp.isValid());
        assertTrue(resp.getAiObject().hasDigitalLink());
    }

    @Test
    @DisplayName("Digital Link: custom-config output round-trips (build-time verification passes)")
    void digitalLinkCustomConfigRoundTrips() {
        BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder()
                .baseUrl("https://example.com/resolver")
                .addQueryParam("context", "retail")
                .fragment("s2")
                .build();
        String dl = GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC")
                .buildDigitalLinkUri(cfg);
        ParseResult resp = PARSER.parse(dl);
        assertTrue(resp.isValid());
        assertTrue(resp.getAiObject().hasDigitalLink());
    }

    @Test
    @DisplayName("Digital Link: qualifiers are emitted in canonical §4.9 order regardless of insertion order")
    void digitalLinkQualifiersReorderedCanonically() {
        // serial (21) added before batch (10); canonical order is batch then serial
        String dl = GaiaBuilder.create()
                .ai("01", "09506000134352")
                .ai("21", "SER1")
                .ai("10", "LOT-ABC")
                .buildDigitalLinkUri();
        assertEquals("https://id.gs1.org/01/09506000134352/10/LOT-ABC/21/SER1", dl);

        // identical result when supplied in canonical order
        String inOrder = GaiaBuilder.create()
                .ai("01", "09506000134352")
                .ai("10", "LOT-ABC")
                .ai("21", "SER1")
                .buildDigitalLinkUri();
        assertEquals(inOrder, dl);
    }

    @Test
    @DisplayName("Digital Link: no primary identification key throws")
    void digitalLinkNoPrimaryThrows() {
        // AI 90 (internal) is a valid standalone element but is not a DL primary key
        assertThrows(GaiaBuilderException.class,
                () -> GaiaBuilder.create().ai("90", "INTERNAL1").buildDigitalLinkUri());
    }

    // -------------------------------------------------------------------------
    // Non-throwing tryBuild*
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("tryBuildElementString: success carries the value and no errors")
    void tryBuildElementStringSuccess() {
        BuildResult r = GaiaBuilder.create().ai("01", "09506000134352").tryBuildElementString();
        assertTrue(r.isSuccess());
        assertEquals("0109506000134352", r.getValue());
        assertNull(r.getMessage());
        assertTrue(r.getErrors().isEmpty());
    }

    @Test
    @DisplayName("tryBuildElementString: failure carries errors, not an exception")
    void tryBuildElementStringFailure() {
        BuildResult r = GaiaBuilder.create().ai("01", "09506000134350").tryBuildElementString();
        assertFalse(r.isSuccess());
        assertNull(r.getValue());
        assertNotNull(r.getMessage());
        assertFalse(r.getErrors().isEmpty());
    }

    @Test
    @DisplayName("tryBuildDigitalLinkUri: success carries the canonical URI")
    void tryBuildDigitalLinkUriSuccess() {
        BuildResult r = GaiaBuilder.create().ai("01", "09506000134352").tryBuildDigitalLinkUri();
        assertTrue(r.isSuccess());
        assertEquals("https://id.gs1.org/01/09506000134352", r.getValue());
    }

    @Test
    @DisplayName("tryBuildDigitalLinkUri: no primary key fails without throwing")
    void tryBuildDigitalLinkUriFailure() {
        BuildResult r = GaiaBuilder.create().ai("90", "INTERNAL1").tryBuildDigitalLinkUri();
        assertFalse(r.isSuccess());
        assertNull(r.getValue());
        assertNotNull(r.getMessage());
    }

    // -------------------------------------------------------------------------
    // AI validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ai: an over-long AI is rejected eagerly (not silently re-tokenised)")
    void aiRejectsOverLongAi() {
        // "99999" would otherwise be parsed as AI 99 + "999..." — a different AI than asked for
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai("99999", "X"));
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai("9999", "X"));
    }

    @Test
    @DisplayName("ai: a non-numeric / unknown AI is rejected eagerly")
    void aiRejectsUnknownAi() {
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai("ZZ", "X"));
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai("1", "X"));
    }

    @Test
    @DisplayName("ai: null AI or value is rejected")
    void aiRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai(null, "X"));
        assertThrows(IllegalArgumentException.class, () -> GaiaBuilder.create().ai("01", null));
    }

    // -------------------------------------------------------------------------
    // Error-message language
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("language: content errors are localized and differ from English")
    void languageLocalizesContentErrors() {
        // same invalid check digit, two languages → two different messages, both non-blank
        String en = GaiaBuilder.create().language(GaiaConstants.Language.ENGLISH)
                .ai("01", "09506000134350").tryBuildElementString()
                .getErrors().get(0).getMessage();
        String fr = GaiaBuilder.create().language(GaiaConstants.Language.FRENCH)
                .ai("01", "09506000134350").tryBuildElementString()
                .getErrors().get(0).getMessage();

        assertNotNull(en);
        assertNotNull(fr);
        assertFalse(en.isBlank());
        assertFalse(fr.isBlank());
        assertNotEquals(en, fr, "French error message should differ from English");
    }

    @Test
    @DisplayName("language: null is ignored, default (English) retained")
    void languageNullIgnored() {
        String def = GaiaBuilder.create()
                .ai("01", "09506000134350").tryBuildElementString()
                .getErrors().get(0).getMessage();
        String withNull = GaiaBuilder.create().language(null)
                .ai("01", "09506000134350").tryBuildElementString()
                .getErrors().get(0).getMessage();
        assertEquals(def, withNull);
    }

    // -------------------------------------------------------------------------
    // BuilderDigitalLinkConfig
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("BuilderDigitalLinkConfig: non-http(s) scheme is rejected")
    void configRejectsBadScheme() {
        assertThrows(IllegalArgumentException.class,
                () -> BuilderDigitalLinkConfig.builder().scheme("ftp").build());
    }

    @Test
    @DisplayName("BuilderDigitalLinkConfig: baseUrl splits scheme, domain, and path prefix")
    void configBaseUrlSplit() {
        BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder().baseUrl("http://id.example.org:8080/r").build();
        assertEquals("http", cfg.getScheme());
        assertEquals("id.example.org:8080", cfg.getDomain());
        assertEquals("/r", cfg.getPathPrefix());
    }
}
