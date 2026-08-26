package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;
import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.dataset.Iso4217Data;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code iso4217} — ISO 4217 numeric currency code.
 *
 * <p>The value must be exactly 3 digits and must match a code present in the bundled
 * {@code iso_4217.json} reference data (e.g. {@code "036"} for Australian Dollar).
 * When the reference data is unavailable the check falls back to the range {@code 001–999}.
 */
public final class Iso4217Validator implements ComponentFormatInterface {

    public static final Iso4217Validator INSTANCE = new Iso4217Validator();

    private Iso4217Validator() {}

    static final String ERR_CODE_LENGTH    = "GE-C071";
    static final String ERR_CODE_NON_DIGIT = "GE-C072";
    static final String ERR_CODE_UNKNOWN   = "GE-C073";
    static final String ERR_CODE_RANGE     = "GE-C074";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 3)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        for (int i = 0; i < 3; i++) {
            if (!CharUtils.isDigit(s.charAt(i)))
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NON_DIGIT, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        }
        if (!Iso4217Data.BY_NUMERIC.isEmpty()) {
            if (!Iso4217Data.BY_NUMERIC.containsKey(s))
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_UNKNOWN, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        } else {
            // Reference data unavailable — fall back to range check
            int val = IntUtils.parseDigits(s, 0, 3);
            if (val < 1 || val > 999)
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_RANGE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        }
        return List.of();
    }
}
