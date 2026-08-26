package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link Iso5218Data} — ISO/IEC 5218 sex codes. */
@DisplayName("Iso5218Data")
class Iso5218DataTest {

    @Test
    @DisplayName("declares the four standard codes")
    void standardCodes() {
        assertTrue(Iso5218Data.forCode("0").isPresent()); // not known
        assertTrue(Iso5218Data.forCode("1").isPresent()); // male
        assertTrue(Iso5218Data.forCode("2").isPresent()); // female
        assertTrue(Iso5218Data.forCode("9").isPresent()); // not applicable
    }

    @Test
    @DisplayName("rejects non-standard codes")
    void unknownCode() {
        assertTrue(Iso5218Data.forCode("3").isEmpty());
    }
}
