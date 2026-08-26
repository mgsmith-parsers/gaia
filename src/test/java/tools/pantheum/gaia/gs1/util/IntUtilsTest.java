package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link IntUtils} — digit substring parsing. */
@DisplayName("IntUtils")
class IntUtilsTest {

    @Test
    @DisplayName("parseDigits extracts the numeric value of a substring")
    void parseDigits() {
        assertEquals(23, IntUtils.parseDigits("12345", 1, 3));
        assertEquals(12345, IntUtils.parseDigits("12345", 0, 5));
        assertEquals(5, IntUtils.parseDigits("12345", 4, 5));
    }
}
