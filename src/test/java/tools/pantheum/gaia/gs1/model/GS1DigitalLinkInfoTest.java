package tools.pantheum.gaia.gs1.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1DigitalLinkInfo} — Digital Link URI metadata. */
@DisplayName("GS1DigitalLinkInfo")
class GS1DigitalLinkInfoTest {

    @Test
    @DisplayName("holds the input URI and derives the URL parts from it")
    void partsDerivedFromUri() {
        URI uri = URI.create("https://example.com/01/09506000134352/10/ABC?17=271231");
        GS1DigitalLinkInfo info = new GS1DigitalLinkInfo(uri);
        assertSame(uri, info.getUri(), "The input URI must be held as received");
        assertNotNull(info.getUrl());
        assertEquals("https", info.getScheme());
        assertEquals("example.com", info.getDomain());
        assertEquals("/01/09506000134352/10/ABC", info.getPath());
        assertEquals("17=271231", info.getQuery());
        assertTrue(info.hasQuery());
    }

    @Test
    @DisplayName("a URI without a query has no query part")
    void noQuery() {
        GS1DigitalLinkInfo info = new GS1DigitalLinkInfo(
                URI.create("http://example.com/01/09506000134352"));
        assertNull(info.getQuery());
        assertFalse(info.hasQuery());
    }

    @Test
    @DisplayName("a null URI is rejected")
    void nullUriRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GS1DigitalLinkInfo(null));
    }

    @Test
    @DisplayName("a URI that is not a valid URL is rejected")
    void nonUrlUriRejected() {
        // relative URIs cannot be converted to a URL
        assertThrows(IllegalArgumentException.class,
                () -> new GS1DigitalLinkInfo(URI.create("/01/09506000134352")));
    }

    @Test
    @DisplayName("toString includes the URI")
    void toStringIncludesUri() {
        GS1DigitalLinkInfo info = new GS1DigitalLinkInfo(URI.create("https://example.com/x"));
        assertTrue(info.toString().contains("https://example.com/x"));
    }
}
