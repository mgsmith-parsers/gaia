package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code yymmddhh} — a YYMMDDHH date-and-hour value used in AI 8008 (PROD TIME).
 *
 * <p>Rules:
 * <ul>
 *   <li>Length must be exactly 8</li>
 *   <li>First 6 digits (YYMMDD): validated as a strict calendar date via {@link YymmddValidator}</li>
 *   <li>Last 2 digits (HH): hour in range 00–23</li>
 * </ul>
 */
public final class YymmddHhValidator implements ComponentFormatInterface {

    public static final YymmddHhValidator INSTANCE = new YymmddHhValidator();

    private YymmddHhValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C096";
    static final String ERR_CODE_DATE   = "GE-C097";
    static final String ERR_CODE_HOUR   = "GE-C098";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 8)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (!YymmddValidator.isValid(s.substring(0, 6)))  // first 6 digits (YYMMDD)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_DATE, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int hh = IntUtils.parseDigits(s, 6, 8);
        if (hh < 0 || hh > 23)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_HOUR, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
