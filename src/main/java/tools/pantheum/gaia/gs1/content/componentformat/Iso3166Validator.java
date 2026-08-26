package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;
import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code iso3166} — ISO 3166-1 numeric country code.
 *
 * <p>The value must be exactly 3 digits and must match a code present in the bundled
 * {@code iso_3166.json} reference data (e.g. {@code "036"} for Australia).
 * When the reference data is unavailable the check falls back to the range {@code 001–999}.
 */
public final class Iso3166Validator implements ComponentFormatInterface {

    public static final Iso3166Validator INSTANCE = new Iso3166Validator();

    private Iso3166Validator() {}

    // Each distinct failure condition has its own error code.
    static final String ERR_CODE_LENGTH    = "GE-C054";  // wrong length
    static final String ERR_CODE_NON_DIGIT = "GE-C055";  // non-digit characters
    static final String ERR_CODE_UNKNOWN   = "GE-C168";  // not a recognised code
    static final String ERR_CODE_RANGE     = "GE-C056";  // out of range (reference data unavailable)

    // Each failure carries only its code; the localized message (using {value}) lives in the catalogue.
    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        String code = failureCode(s);
        return code == null ? List.of()
                : List.of(ErrorRegistry.INSTANCE.create(code, ai, position,
                        Map.of("ai", ai, "component", component, "format", format, "value", s), language));
    }

    /** Returns {@code true} if {@code s} is a valid ISO 3166-1 numeric country code (used by {@link Iso3166ListValidator}). */
    static boolean isValid(String s) {
        return failureCode(s) == null;
    }

    /** The failing condition's catalogue code, or {@code null} if the value is valid. */
    private static String failureCode(String s) {
        if (s.length() != 3) return ERR_CODE_LENGTH;
        for (int i = 0; i < 3; i++) {
            if (!CharUtils.isDigit(s.charAt(i))) return ERR_CODE_NON_DIGIT;
        }
        if (!Iso3166Data.NUMERIC_TO_NAME.isEmpty()) {
            if (!Iso3166Data.NUMERIC_TO_NAME.containsKey(s)) return ERR_CODE_UNKNOWN;
        } else {
            // Reference data unavailable — fall back to range check
            int val = IntUtils.parseDigits(s, 0, 3);
            if (val < 1 || val > 999) return ERR_CODE_RANGE;
        }
        return null;
    }
}
