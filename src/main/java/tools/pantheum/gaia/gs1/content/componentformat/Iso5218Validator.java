package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.dataset.Iso5218Data;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code iso5218} — ISO 5218 sex code.
 *
 * <p>Permitted values are defined in {@link Iso5218Data}:
 * {@code "0"} (not known), {@code "1"} (male), {@code "2"} (female),
 * {@code "9"} (not applicable).
 */
public final class Iso5218Validator implements ComponentFormatInterface {

    public static final Iso5218Validator INSTANCE = new Iso5218Validator();

    private Iso5218Validator() {}

    static final String ERR_CODE_LENGTH = "GE-C075";
    static final String ERR_CODE_CODE   = "GE-C076";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 1)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (!Iso5218Data.CODE_TO_NAME.containsKey(s))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_CODE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
