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
 * Validates {@code yymmdd} — a strict YYMMDD calendar date where day must be 01 or later.
 *
 * <p>Rules:
 * <ul>
 *   <li>Length must be exactly 6</li>
 *   <li>Month (MM): 01–12</li>
 *   <li>Day   (DD): 01–last valid day of the month (00 is not permitted)</li>
 * </ul>
 *
 * <p>Two-digit year resolution follows the GS1 sliding-window rule: the year is
 * mapped to the value within ±49/50 years of the current reference year
 * (see {@link tools.pantheum.gaia.gs1.util.DateUtils#resolveYear(int)}).
 */
public final class YymmddValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final YymmddValidator INSTANCE = new YymmddValidator();

    private YymmddValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C099";
    static final String ERR_CODE_MONTH  = "GE-C100";
    static final String ERR_CODE_DAY    = "GE-C101";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        String code = failureCode(s);
        return code == null ? List.of()
                : List.of(ErrorRegistry.INSTANCE.create(code, ai, position,
                        Map.of("ai", ai, "component", component, "format", format, "value", s), language));
    }

    /** Returns {@code true} if {@code s} is a valid strict YYMMDD date (used by embedding validators). */
    static boolean isValid(String s) {
        return failureCode(s) == null;
    }

    /** The failing condition's catalogue code, or {@code null} if the value is valid. */
    private static String failureCode(String s) {
        if (s.length() != 6) return ERR_CODE_LENGTH;
        int yy = IntUtils.parseDigits(s, 0, 2);
        int mm = IntUtils.parseDigits(s, 2, 4);
        int dd = IntUtils.parseDigits(s, 4, 6);
        if (mm < 1 || mm > 12) return ERR_CODE_MONTH;
        int max = DateUtils.maxDayYy(yy, mm);
        if (dd < 1 || dd > max) return ERR_CODE_DAY;
        return null;
    }
}
