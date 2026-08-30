package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.DateUtils;
import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code yyyymmdd} — a full-year YYYYMMDD calendar date.
 *
 * <p>Rules:
 * <ul>
 *   <li>Length must be exactly 8</li>
 *   <li>Year  (YYYY): 0001–9999</li>
 *   <li>Month (MM):   01–12</li>
 *   <li>Day   (DD):   01–last valid day of the month</li>
 * </ul>
 *
 * <p>Used in AIs 7250 (CERT # REL) and 7251 (CERT # ABI).
 */
public final class YyyymmddValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final YyyymmddValidator INSTANCE = new YyyymmddValidator();

    private YyyymmddValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C102";
    static final String ERR_CODE_YEAR   = "GE-C103";
    static final String ERR_CODE_MONTH  = "GE-C104";
    static final String ERR_CODE_DAY    = "GE-C105";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 8)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int yyyy = IntUtils.parseDigits(s, 0, 4);
        int mm   = IntUtils.parseDigits(s, 4, 6);
        int dd   = IntUtils.parseDigits(s, 6, 8);
        if (yyyy < 1 || yyyy > 9999)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_YEAR, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (mm < 1 || mm > 12)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_MONTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int max = DateUtils.maxDayFull(yyyy, mm);
        if (dd < 1 || dd > max)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_DAY, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
