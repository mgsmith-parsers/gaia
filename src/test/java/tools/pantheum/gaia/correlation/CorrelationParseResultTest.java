package tools.pantheum.gaia.correlation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link CorrelationParseResult}. */
@DisplayName("CorrelationParseResult")
class CorrelationParseResultTest {

    private final CorrelationIdParser parser = new CorrelationIdParser();

    @Test
    @DisplayName("exposes correlation info and stripped payload")
    void accessors() {
        CorrelationParseResult r = parser.parse("00000001~PAYLOAD");
        assertTrue(r.hasCorrelationId());
        assertEquals("00000001", r.getCorrelationInfo().getId());
        assertEquals("PAYLOAD", r.getStrippedPayload());
    }

    @Test
    @DisplayName("hasCorrelationId is false when no prefix was present")
    void noPrefix() {
        CorrelationParseResult r = parser.parse("PAYLOAD");
        assertFalse(r.hasCorrelationId());
    }
}
