package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code pcenc} — a percent-encoded string conforming to RFC 3986.
 *
 * <p>Each character must be one of:
 * <ul>
 *   <li>An RFC 3986 unreserved character: {@code A–Z a–z 0–9 - _ . ~}</li>
 *   <li>A {@code +} character</li>
 *   <li>A percent-encoded sequence: {@code %HH} where {@code H} is a hexadecimal digit</li>
 * </ul>
 */
public final class PcencValidator implements ComponentFormatInterface {

    public static final PcencValidator INSTANCE = new PcencValidator();

    private PcencValidator() {}

    static final String ERR_CODE_INCOMPLETE   = "GE-C082";
    static final String ERR_CODE_INVALID_HEX  = "GE-C083";
    static final String ERR_CODE_INVALID_CHAR = "GE-C084";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (CharUtils.isUnreservedPcenc(c)) continue;
            if (c == '+') continue;
            if (c == '%') {
                if (i + 2 >= s.length())
                    return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_INCOMPLETE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
                char h1 = s.charAt(++i);
                char h2 = s.charAt(++i);
                if (!CharUtils.isHexDigit(h1) || !CharUtils.isHexDigit(h2))
                    return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_INVALID_HEX, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
            } else {
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_INVALID_CHAR, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
            }
        }
        return List.of();
    }
}
