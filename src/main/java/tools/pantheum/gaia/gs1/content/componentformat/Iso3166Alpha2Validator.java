package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;

import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code iso3166alpha2} — ISO 3166-1 alpha-2 country code.
 *
 * <p>The value must be exactly 2 uppercase letters and must match a code present in the
 * bundled {@code iso_3166.json} reference data (e.g. {@code "AU"} for Australia).
 * When the reference data is unavailable the check falls back to two-uppercase-letters only.
 */
public final class Iso3166Alpha2Validator implements ComponentFormatInterface {

    public static final Iso3166Alpha2Validator INSTANCE = new Iso3166Alpha2Validator();

    private Iso3166Alpha2Validator() {}

    static final String ERR_CODE_LENGTH  = "GE-C068";
    static final String ERR_CODE_LETTERS = "GE-C069";
    static final String ERR_CODE_UNKNOWN = "GE-C070";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 2)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (!CharUtils.isUpperAlpha(s.charAt(0)) || !CharUtils.isUpperAlpha(s.charAt(1)))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LETTERS, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (!Iso3166Data.ALPHA2_TO_NAME.isEmpty()) {
            if (!Iso3166Data.ALPHA2_TO_NAME.containsKey(s))
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_UNKNOWN, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        }
        return List.of();
    }
}
