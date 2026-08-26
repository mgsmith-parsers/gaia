package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.gs1.model.GS1DigitalLinkInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1DLParser} — the full Digital Link parsing pipeline. */
@DisplayName("GS1DLParser")
class GS1DLParserTest {

    private final GS1DLParser parser = new GS1DLParser();

    private GS1AIObject parse(String input) {
        return parse(input, GS1Constants.ParseMode.INTERPRETATION);
    }

    private GS1AIObject parse(String input, GS1Constants.ParseMode mode) {
        return parser.parse(input, mode, ParseConfig.defaultConfig());
    }

    // -------------------------------------------------------------------------
    // Valid Digital Link URIs
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("a valid Digital Link URI populates elements, roles, and URL parts")
    void validUriPopulatesObject() {
        GS1AIObject obj = parse("https://example.com/01/09506000134352/10/ABC?17=271231");
        assertTrue(obj.isValid());
        assertEquals(3, obj.size());

        assertEquals("09506000134352", obj.get("01").getValue());
        assertEquals(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY,
                obj.get("01").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.KEY_QUALIFIER,
                obj.get("10").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE,
                obj.get("17").getDigitalLinkAIType());

        assertTrue(obj.hasDigitalLink());
        GS1DigitalLinkInfo info = obj.getDigitalLinkInfo();
        assertEquals("https", info.getScheme());
        assertEquals("example.com", info.getDomain());
        assertEquals("/01/09506000134352/10/ABC", info.getPath());
        assertEquals("17=271231", info.getQuery());
        assertEquals(GS1Constants.GS1ContentType.GS1_DIGITAL_LINK, obj.getContentType());
    }

    @Test
    @DisplayName("INTERPRETATION mode enriches the extracted elements")
    void interpretationModeEnriches() {
        GS1AIObject obj = parse("https://example.com/01/09506000134352");
        assertTrue(obj.isValid());
        assertFalse(obj.get("01").getInterpretations().isEmpty(),
                "The GTIN element must carry interpretations");
    }

    @Test
    @DisplayName("getCanonicalDigitalLink rebuilds the canonical URI (spec §4.12)")
    void canonicalDigitalLink() {
        // Custom domain + linkType, exactly the spec §4.12 example.
        GS1AIObject obj = parse("http://example.com/01/09520123456788/22/2A?linkType=gs1:traceability");
        assertEquals("https://id.gs1.org/01/09520123456788/22/2A", obj.getCanonicalDigitalLink());
    }

    @Test
    @DisplayName("getCanonicalDigitalLink drops the custom path stem and sorts query keys lexically")
    void canonicalDropsStemAndSortsQuery() {
        GS1AIObject obj = parse(
                "https://brand.example.com/shop/01/09506000134352/10/LOT-ABC?3103=000195&17=271231");
        // qualifiers in path, data attributes sorted lexically: 17 before 3103
        assertEquals("https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231&3103=000195",
                obj.getCanonicalDigitalLink());
    }

    @Test
    @DisplayName("getCanonicalDigitalLink returns null for an element-string parse")
    void canonicalNullForElementString() {
        GS1AIObject obj = new tools.pantheum.gaia.gs1.GS1AIParser()
                .parse("0109506000134352", GS1Constants.ParseMode.CONTENT,
                        ParseConfig.defaultConfig());
        assertNull(obj.getCanonicalDigitalLink());
    }

    @Test
    @DisplayName("CONTENT mode validates but does not enrich")
    void contentModeDoesNotEnrich() {
        GS1AIObject obj = parse("https://example.com/01/09506000134352",
                GS1Constants.ParseMode.CONTENT);
        assertTrue(obj.isValid());
        assertTrue(obj.get("01").getInterpretations().isEmpty());
    }

    @Test
    @DisplayName("a second primary key in the query is a data attribute (spec §5.11)")
    void giaiPlusGtin() {
        GS1AIObject obj = parse("https://example.com/8004/9506000123?01=09506000134352");
        assertTrue(obj.isValid());
        assertEquals(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY,
                obj.get("8004").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE,
                obj.get("01").getDigitalLinkAIType());
    }

    // -------------------------------------------------------------------------
    // Pipeline depth
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("a wrong GTIN check digit passes SYNTAX mode but fails CONTENT mode")
    void checkDigitValidatedInContentStage() {
        String input = "https://example.com/01/09506000134353"; // bad check digit
        assertTrue(parse(input, GS1Constants.ParseMode.SYNTAX).isValid(),
                "Check digits are not validated in SYNTAX mode");
        GS1AIObject obj = parse(input, GS1Constants.ParseMode.CONTENT);
        assertFalse(obj.isValid());
        assertTrue(obj.getAllErrors().stream().anyMatch(e -> "GE-C003".equals(e.getId())));
    }

    @Test
    @DisplayName("a 13-digit GTIN value is rejected (spec §4.1 requires 14 digits)")
    void shortGtinRejected() {
        GS1AIObject obj = parse("https://example.com/01/9506000134352");
        assertFalse(obj.isValid());
    }

    @Test
    @DisplayName("missing required AIs are detected by the structural validator")
    void requiresValidationApplies() {
        // AI (8111) requires AI (255) — a GCN primary key would satisfy it; (01) does not.
        GS1AIObject obj = parse("https://example.com/01/09506000134352?8111=0001");
        assertFalse(obj.isValid());
        assertTrue(obj.getAllErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())));
    }

    // -------------------------------------------------------------------------
    // Invalid URLs and structures (GE-L001)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("a malformed URI produces GE-L001")
    void malformedUriRejected() {
        GS1AIObject obj = parse("https://exa mple.com/01/x");
        assertFalse(obj.isValid());
        assertFalse(obj.hasDigitalLink());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-L001".equals(e.getId())));
    }

    @Test
    @DisplayName("a non-http(s) scheme produces GE-L002")
    void nonHttpSchemeRejected() {
        GS1AIObject obj = parse("ftp://example.com/01/09506000134352");
        assertFalse(obj.isValid());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-L002".equals(e.getId())));
    }

    @Test
    @DisplayName("a URL without a host produces GE-L003")
    void missingHostRejected() {
        GS1AIObject obj = parse("https:///01/09506000134352");
        assertFalse(obj.isValid());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-L003".equals(e.getId())));
    }

    @Test
    @DisplayName("a URL without a primary key produces GE-L004 but keeps the URL parts")
    void noPrimaryKeyKeepsInfo() {
        GS1AIObject obj = parse("https://example.com/about");
        assertFalse(obj.isValid());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-L004".equals(e.getId())));
        assertFalse(obj.hasDigitalLink(), "no primary key — not a usable Digital Link");
        assertNotNull(obj.getDigitalLinkInfo(), "URL decomposition survives structural errors");
        assertEquals(0, obj.size());
    }

    @Test
    @DisplayName("qualifier order violations fail before content validation")
    void qualifierOrderFailsEarly() {
        GS1AIObject obj = parse("https://example.com/01/09506000134352/21/SER/10/LOT");
        assertFalse(obj.isValid());
        assertTrue(obj.getErrors().stream().anyMatch(e -> "GE-L012".equals(e.getId())));
        assertTrue(obj.get("01").getInterpretations().isEmpty(),
                "Interpretation must not run after a syntax-stage error");
    }
}
