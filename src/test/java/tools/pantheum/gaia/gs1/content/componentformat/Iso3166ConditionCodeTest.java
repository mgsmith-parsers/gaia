package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Each Iso3166Validator failure condition carries its own error code. */
@DisplayName("Iso3166Validator per-condition error codes")
class Iso3166ConditionCodeTest {

    private static List<GaiaError> errorsFor(String value) {
        return Iso3166Validator.INSTANCE.validate(value, "3922", 0, "iso3166", "iso3166",
                GaiaConstants.Language.ENGLISH);
    }

    private static String idFor(String value) {
        List<GaiaError> errors = errorsFor(value);
        assertEquals(1, errors.size(), "exactly one error expected for " + value);
        return errors.get(0).getId();
    }

    @Test
    @DisplayName("wrong length, non-digit, and unrecognised each map to distinct codes")
    void distinctCodes() {
        assertEquals("GE-C054", idFor("12"),  "wrong length");
        assertEquals("GE-C055", idFor("12a"), "non-digit characters");
        assertEquals("GE-C168", idFor("000"), "unrecognised code");
    }

    @Test
    @DisplayName("a recognised code yields no errors")
    void validCode() {
        assertTrue(errorsFor("036").isEmpty());
    }
}
