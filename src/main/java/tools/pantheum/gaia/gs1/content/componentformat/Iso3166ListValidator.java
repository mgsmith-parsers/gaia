package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates {@code iso3166list} — a concatenated list of ISO 3166-1 numeric country codes.
 *
 * <p>The value must be a non-empty sequence of 3-digit country codes (001–999), so its
 * length must be a non-zero multiple of 3. Each invalid code in the list is reported
 * as its own error.
 */
public final class Iso3166ListValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final Iso3166ListValidator INSTANCE = new Iso3166ListValidator();

    private Iso3166ListValidator() {}

    static final String ERR_CODE_LENGTH   = "GE-C107";  // length not a non-zero multiple of 3
    static final String ERR_CODE_BAD_CODE = "GE-C108";  // an invalid country code in the list

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.isEmpty() || s.length() % 3 != 0)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        List<GaiaError> errors = new ArrayList<>();
        for (int i = 0; i < s.length(); i += 3) {
            if (!Iso3166Validator.isValid(s.substring(i, i + 3))) {
                errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_BAD_CODE, ai, position,
                        Map.of("ai", ai, "component", component, "format", format, "value", s,
                                "position", String.valueOf(i / 3 + 1)), language));
            }
        }
        return errors;
    }
}
