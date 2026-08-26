package tools.pantheum.gaia.correlation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link CorrelationInfo}. */
@DisplayName("CorrelationInfo")
class CorrelationInfoTest {

    @Test
    @DisplayName("holds the correlation ID")
    void holdsId() {
        CorrelationInfo info = new CorrelationInfo("12345678");
        assertEquals("12345678", info.getId());
    }

    @Test
    @DisplayName("toString includes the ID")
    void toStringIncludesId() {
        assertTrue(new CorrelationInfo("12345678").toString().contains("12345678"));
    }
}
