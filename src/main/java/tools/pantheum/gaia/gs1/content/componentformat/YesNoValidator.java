package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code yesno} — a boolean flag encoded as a single digit.
 *
 * <p>Permitted values:
 * <ul>
 *   <li>{@code 0} — no / false</li>
 *   <li>{@code 1} — yes / true</li>
 * </ul>
 *
 * <p>Used in AIs 4321 (DANGEROUS GOODS), 4322 (AUTH LEAVE), and 4323 (SIG REQUIRED).
 */
public final class YesNoValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final YesNoValidator INSTANCE = new YesNoValidator();

    private YesNoValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C091";
    static final String ERR_CODE_VALUE  = "GE-C092";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 1)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        char c = s.charAt(0);
        if (c != '0' && c != '1')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_VALUE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
