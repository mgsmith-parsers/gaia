package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code winding} — the winding direction of a reel in AI 8001 (DIMENSIONS).
 *
 * <p>Permitted values per GS1 General Specifications section 3.9.1:
 * <ul>
 *   <li>{@code 0} — face out</li>
 *   <li>{@code 1} — face in</li>
 *   <li>{@code 9} — undefined</li>
 * </ul>
 */
public final class WindingValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final WindingValidator INSTANCE = new WindingValidator();

    private WindingValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C089";
    static final String ERR_CODE_VALUE  = "GE-C090";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 1)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        char c = s.charAt(0);
        if (c != '0' && c != '1' && c != '9')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_VALUE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }

    /**
     * Returns the human-readable name for the given winding direction code,
     * or {@code null} if the code is {@code "9"} (undefined) or unrecognised.
     *
     * @param code single-digit string: {@code "0"}, {@code "1"}, or {@code "9"}
     * @return a new {@code String}
     */
    public static String nameForCode(String code) {
        if ("0".equals(code)) return "Face out";
        if ("1".equals(code)) return "Face in";
        return null; // "9" = undefined — callers should omit the interpretation
    }
}
