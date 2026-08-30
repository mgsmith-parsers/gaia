package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code nozeroprefix} — a numeric component that must not start with a leading zero.
 * Used for the component part number serial in AI 8011 (CPID SERIAL).
 */
public final class NozeroPrefixValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final NozeroPrefixValidator INSTANCE = new NozeroPrefixValidator();

    private NozeroPrefixValidator() {}

    static final String ERR_CODE_LEADING_ZERO = "GE-C081";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() > 1 && s.charAt(0) == '0')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LEADING_ZERO, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
