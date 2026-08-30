package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;

/**
 * Validates {@code importeridx} — the single-character Importer Index in AI 7040 (UIC+EXT).
 *
 * <p>Permitted characters (GS1 General Specifications, AI 7040):
 * <ul>
 *   <li>{@code A–Z} — uppercase letters (identify up to 26 importers)</li>
 *   <li>{@code a–z} — lowercase letters (identify up to 26 further importers)</li>
 *   <li>{@code 0–9} — digits (identify up to 10 further importers)</li>
 *   <li>{@code -} — hyphen (identifies one further importer)</li>
 *   <li>{@code _} — underscore (indicates the importer index does not apply — null value)</li>
 * </ul>
 */
public final class ImporterIdxValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final ImporterIdxValidator INSTANCE = new ImporterIdxValidator();

    private ImporterIdxValidator() {}

    static final String ERR_CODE_LENGTH    = "GE-C066";
    static final String ERR_CODE_CHARACTER = "GE-C067";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 1)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        char c = s.charAt(0);
        if (!Character.isLetterOrDigit(c) && c != '-' && c != '_')
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_CHARACTER, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return List.of();
    }
}
