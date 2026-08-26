package tools.pantheum.gaia.gs1.charset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1CharacterSet} — CSET 39 / 64 / 82 membership. */
@DisplayName("GS1CharacterSet")
class GS1CharacterSetTest {

    @Test
    @DisplayName("CSET 82 contains letters, digits, and '!' but not '#'")
    void cset82Membership() {
        assertTrue(GS1CharacterSet.CSET82.contains('A'));
        assertTrue(GS1CharacterSet.CSET82.contains('z'));
        assertTrue(GS1CharacterSet.CSET82.contains('0'));
        assertTrue(GS1CharacterSet.CSET82.contains('!'));
        assertFalse(GS1CharacterSet.CSET82.contains('#'));
        assertFalse(GS1CharacterSet.CSET82.contains(' '));
    }

    @Test
    @DisplayName("CSET 39 contains '#' but not lowercase letters")
    void cset39Membership() {
        assertTrue(GS1CharacterSet.CSET39.contains('#'));
        assertTrue(GS1CharacterSet.CSET39.contains('A'));
        assertFalse(GS1CharacterSet.CSET39.contains('a'));
    }

    @Test
    @DisplayName("every set publishes its character list")
    void getChars() {
        for (GS1CharacterSet set : GS1CharacterSet.values()) {
            assertNotNull(set.getChars());
            assertFalse(set.getChars().isEmpty());
        }
    }
}
