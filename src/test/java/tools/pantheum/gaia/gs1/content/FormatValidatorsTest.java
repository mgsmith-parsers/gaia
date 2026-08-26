package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.gs1.content.componentformat.YymmddValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link FormatValidators} — the component format registry. */
@DisplayName("FormatValidators")
class FormatValidatorsTest {

    @Test
    @DisplayName("resolves a known format to its validator")
    void knownFormat() {
        assertTrue(FormatValidators.forFormat("yymmdd") instanceof YymmddValidator);
    }

    @Test
    @DisplayName("returns null for unknown or null formats")
    void unknownFormat() {
        assertNull(FormatValidators.forFormat("not-a-format"));
        assertNull(FormatValidators.forFormat(null));
    }
}
