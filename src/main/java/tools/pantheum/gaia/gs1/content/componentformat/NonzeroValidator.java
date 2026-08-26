package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code nonzero} — a numeric component whose value must not be all zeros.
 * Used for width, length, and thickness components in AI 8001 (DIMENSIONS).
 */
public final class NonzeroValidator implements ComponentFormatInterface {

    public static final NonzeroValidator INSTANCE = new NonzeroValidator();

    private NonzeroValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C080";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') return List.of();
        }
        return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
    }
}
