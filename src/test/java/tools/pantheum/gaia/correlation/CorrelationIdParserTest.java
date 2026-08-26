package tools.pantheum.gaia.correlation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link CorrelationIdParser} — optional 8-digit~ prefix stripping. */
@DisplayName("CorrelationIdParser")
class CorrelationIdParserTest {

    private final CorrelationIdParser parser = new CorrelationIdParser();

    @Test
    @DisplayName("strips a valid 8-digit correlation prefix")
    void stripsValidPrefix() {
        CorrelationParseResult r = parser.parse("12345678~0109506000134352");
        assertTrue(r.hasCorrelationId());
        assertEquals("12345678", r.getCorrelationInfo().getId());
        assertEquals("0109506000134352", r.getStrippedPayload());
    }

    @Test
    @DisplayName("passes through input without a prefix")
    void passthroughWithoutPrefix() {
        CorrelationParseResult r = parser.parse("0109506000134352");
        assertFalse(r.hasCorrelationId());
        assertNull(r.getCorrelationInfo());
        assertEquals("0109506000134352", r.getStrippedPayload());
    }

    @Test
    @DisplayName("does not treat a malformed prefix as a correlation ID")
    void malformedPrefixPassthrough() {
        CorrelationParseResult r = parser.parse("1234~0109506000134352");
        assertFalse(r.hasCorrelationId(), "Only an 8-digit prefix qualifies");
        assertEquals("1234~0109506000134352", r.getStrippedPayload());
    }
}
