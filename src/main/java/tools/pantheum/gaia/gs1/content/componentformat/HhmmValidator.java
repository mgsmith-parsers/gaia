package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code hhmm} — HHMM time where HH ∈ [00,23] and MM ∈ [00,59].
 */
public final class HhmmValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final HhmmValidator INSTANCE = new HhmmValidator();

    private HhmmValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C057";
    static final String ERR_CODE_HOUR   = "GE-C058";
    static final String ERR_CODE_MINUTE = "GE-C059";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 4)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int hh = IntUtils.parseDigits(s, 0, 2);
        int mm = IntUtils.parseDigits(s, 2, 4);
        if (hh < 0 || hh > 23) return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_HOUR, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (mm < 0 || mm > 59) return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_MINUTE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
