package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code mmoptss} — an optional-seconds time component used in AI 8008 (PROD TIME).
 *
 * <p>Accepted forms:
 * <ul>
 *   <li>{@code MM}   — minutes only (2 digits, 00–59)</li>
 *   <li>{@code MMSS} — minutes and seconds (4 digits, each 00–59)</li>
 * </ul>
 */
public final class MmoptssValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final MmoptssValidator INSTANCE = new MmoptssValidator();

    private MmoptssValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C077";
    static final String ERR_CODE_MINUTE = "GE-C078";
    static final String ERR_CODE_SECOND = "GE-C079";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 2 && s.length() != 4)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        int mm = IntUtils.parseDigits(s, 0, 2);
        if (mm < 0 || mm > 59)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_MINUTE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (s.length() == 4) {
            int ss = IntUtils.parseDigits(s, 2, 4);
            if (ss < 0 || ss > 59)
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_SECOND, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        }
        return List.of();
    }
}
