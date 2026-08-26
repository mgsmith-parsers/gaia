package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link StringUtils} — string helpers. */
@DisplayName("StringUtils")
class StringUtilsTest {

    @Test
    @DisplayName("isAllZeros is true only for non-empty all-zero strings")
    void isAllZeros() {
        assertTrue(StringUtils.isAllZeros("0"));
        assertTrue(StringUtils.isAllZeros("000000"));
        assertFalse(StringUtils.isAllZeros("010"));
        assertFalse(StringUtils.isAllZeros("ABC"));
    }
}
