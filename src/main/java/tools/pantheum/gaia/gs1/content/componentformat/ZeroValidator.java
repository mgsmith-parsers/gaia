package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code zero} — a single-digit component whose value must be exactly {@code "0"}.
 * Used as a fixed-value pad digit in AI 8003 (GRAI).
 */
public final class ZeroValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final ZeroValidator INSTANCE = new ZeroValidator();

    private ZeroValidator() {}

    static final String ERR_CODE_NON_ZERO = "GE-C106";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (!"0".equals(s))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NON_ZERO, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
