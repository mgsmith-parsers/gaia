package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code posinseqslash} — a position-in-sequence value encoded as {@code "P/T"}.
 *
 * <p>The component is exactly 3 characters (X3 fixed-length), formatted as a single digit,
 * a forward slash, and a single digit — for example {@code "1/3"}, {@code "2/5"}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Format must be exactly {@code digit/digit}</li>
 *   <li>Total (T) must not be zero</li>
 *   <li>Position (P) must not exceed total (P ≤ T)</li>
 * </ul>
 *
 * <p>Used in AI 7258 (BIRTH SEQUENCE).
 */
public final class PosInSeqSlashValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final PosInSeqSlashValidator INSTANCE = new PosInSeqSlashValidator();

    private PosInSeqSlashValidator() {}

    static final String ERR_CODE_FORMAT        = "GE-C085";
    static final String ERR_CODE_NON_DIGIT     = "GE-C086";
    static final String ERR_CODE_TOTAL_ZERO    = "GE-C087";
    static final String ERR_CODE_EXCEEDS_TOTAL = "GE-C088";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 3 || s.charAt(1) != '/')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_FORMAT, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        char p = s.charAt(0);
        char t = s.charAt(2);
        if (!CharUtils.isDigit(p) || !CharUtils.isDigit(t))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NON_DIGIT, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (t == '0')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_TOTAL_ZERO, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (p > t)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_EXCEEDS_TOTAL, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
