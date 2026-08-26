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
 * Validates {@code yymmd0} — a YYMMDD date where {@code DD=00} is permitted.
 *
 * <p>A day value of {@code 00} is a GS1 convention meaning "no specific day is
 * known" or "the last day of the month".
 *
 * <p>Rules:
 * <ul>
 *   <li>Length must be exactly 6</li>
 *   <li>Month (MM): 01–12</li>
 *   <li>Day   (DD): 00–last valid day of the month (00 = end of month)</li>
 * </ul>
 *
 * <p>Used in AIs 11, 12, 13, 15, 16, 17, and others.
 */
public final class Yymmd0Validator implements ComponentFormatInterface {

    public static final Yymmd0Validator INSTANCE = new Yymmd0Validator();

    private Yymmd0Validator() {}

    static final String ERR_CODE_LENGTH = "GE-C093";
    static final String ERR_CODE_MONTH  = "GE-C094";
    static final String ERR_CODE_DAY    = "GE-C095";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 6)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int yy = IntUtils.parseDigits(s, 0, 2);
        int mm = IntUtils.parseDigits(s, 2, 4);
        int dd = IntUtils.parseDigits(s, 4, 6);
        if (mm < 1 || mm > 12)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_MONTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (dd == 0)
            return List.of(); // 00 = last day of the month — always valid for any valid month
        int max = DateUtils.maxDayYy(yy, mm);
        if (dd < 1 || dd > max)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_DAY, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
