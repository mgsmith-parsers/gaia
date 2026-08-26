package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link PcEncUtils} — percent-encoding decode helpers. */
@DisplayName("PcEncUtils")
class PcEncUtilsTest {

    @Test
    @DisplayName("decode decodes percent-encoded UTF-8 sequences")
    void decodesUtf8() {
        assertEquals("François", PcEncUtils.decode("Fran%C3%A7ois"));
        assertEquals("€", PcEncUtils.decode("%E2%82%AC"));
        assertEquals("AB/C%", PcEncUtils.decode("AB%2FC%25"));
    }

    @Test
    @DisplayName("decode passes a literal plus through unchanged (pure RFC 3986)")
    void plusLeftUnchanged() {
        assertEquals("A+B", PcEncUtils.decode("A+B"));
        assertEquals("A+B", PcEncUtils.decode("A%2BB"));
    }

    @Test
    @DisplayName("decode returns a value without percent-encoding unchanged")
    void plainValueUnchanged() {
        assertEquals("ABC123", PcEncUtils.decode("ABC123"));
    }

    @Test
    @DisplayName("decode throws on a malformed percent-encoding")
    void decodeThrowsOnMalformed() {
        assertThrows(IllegalArgumentException.class, () -> PcEncUtils.decode("bad%2"));
        assertThrows(IllegalArgumentException.class, () -> PcEncUtils.decode("bad%GZ"));
    }

    @Test
    @DisplayName("encode keeps unreserved characters and percent-encodes the rest")
    void encode() {
        assertEquals("ABC123-._~", PcEncUtils.encode("ABC123-._~"));
        assertEquals("AB%2FC%25", PcEncUtils.encode("AB/C%"));
        assertEquals("a%20b", PcEncUtils.encode("a b"));
        assertEquals("%E2%82%AC", PcEncUtils.encode("€"));
    }

    @Test
    @DisplayName("encode round-trips through decode for unreserved + reserved characters")
    void encodeDecodeRoundTrip() {
        String raw = "LOT/123 #A%";
        assertEquals(raw, PcEncUtils.decode(PcEncUtils.encode(raw)));
    }

    @Test
    @DisplayName("hasPercentEncoding detects a well-formed %XX sequence")
    void hasPercentEncodingTrue() {
        assertTrue(PcEncUtils.hasPercentEncoding("AB%2FC"));
        assertTrue(PcEncUtils.hasPercentEncoding("%E2%82%AC"));
        assertTrue(PcEncUtils.hasPercentEncoding("%25"));
    }

    @Test
    @DisplayName("hasPercentEncoding is false for plain text, a bare or incomplete percent, and null")
    void hasPercentEncodingFalse() {
        assertFalse(PcEncUtils.hasPercentEncoding("ABC123"));
        assertFalse(PcEncUtils.hasPercentEncoding("50%"));        // bare %
        assertFalse(PcEncUtils.hasPercentEncoding("bad%2"));      // incomplete
        assertFalse(PcEncUtils.hasPercentEncoding("bad%GZ"));     // non-hex
        assertFalse(PcEncUtils.hasPercentEncoding(""));
        assertFalse(PcEncUtils.hasPercentEncoding(null));
    }
}
