package tools.pantheum.gaia.gs1.syntax.digitallink;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link DLSyntaxParser} — AI extraction from Digital Link URIs. */
@DisplayName("DLSyntaxParser")
class DLSyntaxParserTest {

    private final DLSyntaxParser parser = new DLSyntaxParser(AiDefinitionRegistry.getInstance());

    private SyntaxParseResult parse(String input) {
        return parser.parse(input, ParseConfig.defaultConfig());
    }

    private static GS1AIObjectElement element(SyntaxParseResult r, String ai) {
        return r.getElements().stream()
                .filter(e -> e.getAi().equals(ai)).findFirst().orElse(null);
    }

    @Test
    @DisplayName("extracts primary key, qualifiers, and data attributes with their roles")
    void fullExtraction() {
        SyntaxParseResult r = parse(
                "https://example.com/01/09506000134352/10/ABC/21/12345?17=271231");
        assertFalse(r.hasSyntaxErrors());
        assertEquals(4, r.getElements().size());
        assertEquals(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY,
                element(r, "01").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.KEY_QUALIFIER,
                element(r, "10").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.KEY_QUALIFIER,
                element(r, "21").getDigitalLinkAIType());
        assertEquals(GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE,
                element(r, "17").getDigitalLinkAIType());
        assertEquals("09506000134352", element(r, "01").getValue());
        assertEquals("ABC", element(r, "10").getValue());
        assertEquals("271231", element(r, "17").getValue());
    }

    @Test
    @DisplayName("a custom path stem before the primary key is tolerated (spec §6.1.1)")
    void customPathStem() {
        SyntaxParseResult r = parse("https://brand.example.com/products/au/01/09506000134352");
        assertFalse(r.hasSyntaxErrors());
        assertEquals("01", r.getElements().get(0).getAi());
    }

    @Test
    @DisplayName("a trailing slash is tolerated")
    void trailingSlash() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352/10/ABC/");
        assertFalse(r.hasSyntaxErrors());
        assertEquals(2, r.getElements().size());
    }

    @Test
    @DisplayName("percent-encoded characters in values are decoded (spec §4.2)")
    void percentDecoding() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352/10/AB%2FC%25");
        assertFalse(r.hasSyntaxErrors());
        assertEquals("AB/C%", element(r, "10").getValue());
    }

    @Test
    @DisplayName("a malformed percent-encoding in a value produces GE-L001 (spec §4.2)")
    void malformedPercentEncodingRejected() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352/10/AB%2");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L001".equals(e.getId())));
    }

    @Test
    @DisplayName("reserved keywords and extension parameters in the query are ignored")
    void extensionParametersIgnored() {
        SyntaxParseResult r = parse(
                "https://example.com/01/09506000134352?linkType=gs1:pip&23P=12098&17=271231");
        assertFalse(r.hasSyntaxErrors());
        assertEquals(2, r.getElements().size(), "Only AI (01) and AI (17) become elements");
        assertNotNull(element(r, "17"));
    }

    @Test
    @DisplayName("an all-numeric query key that is not an AI produces GE-L006 (spec §4.10.1)")
    void numericNonAiQueryKeyRejected() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352?236=12098");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L006".equals(e.getId())));
    }

    @Test
    @DisplayName("a known AI not valid as a data attribute produces GE-L006 in the query (spec §4.10)")
    void nonDataAttributeQueryKeyRejected() {
        // AI (21) serial number is a key qualifier (validAsDataAttribute = false),
        // so it may not appear as a query data attribute.
        SyntaxParseResult r = parse("https://example.com/01/09506000134352?21=SER123");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L006".equals(e.getId())));
    }

    @Test
    @DisplayName("a path without a primary identification key produces GE-L004")
    void noPrimaryKey() {
        SyntaxParseResult r = parse("https://example.com/some/page");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getElements().isEmpty());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L004".equals(e.getId())));
        assertTrue(r.hasDigitalLink(), "The URL itself was valid — info is still populated");
    }

    @Test
    @DisplayName("unpaired path segments after the primary key produce GE-L005")
    void unpairedSegments() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352/10");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L005".equals(e.getId())));
    }

    @Test
    @DisplayName("qualifier order violations produce GE-L012 (spec §4.9)")
    void qualifierOrderViolation() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352/21/SER/10/LOT");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L012".equals(e.getId())));
    }

    @Test
    @DisplayName("AI (8014) in the query is not valid as a data attribute, GE-L006 (spec §4.10)")
    void bannedAiRejected() {
        SyntaxParseResult r = parse("https://example.com/01/09506000134352?8014=950600013435200001");
        assertTrue(r.hasSyntaxErrors());
        assertTrue(r.getErrors().stream().anyMatch(e -> "GE-L006".equals(e.getId())));
    }

    @Test
    @DisplayName("a non-URL input produces GE-L001 and no Digital Link info")
    void invalidUrl() {
        SyntaxParseResult r = parse("ftp://example.com/01/09506000134352");
        assertTrue(r.hasSyntaxErrors());
        assertFalse(r.hasDigitalLink());
        assertNull(r.getDigitalLinkInfo());
    }
}
