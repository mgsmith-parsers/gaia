package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.dataset.AsfisData;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7008 — Aquatic Species (AQUATIC SPECIES).
 *
 * <p>The standard pipeline already verifies that the value is 1–3 characters
 * from the GS1 AI encodable character set. This validator checks that the code
 * is a recognised FAO Alpha-3 species code from the ASFIS List of Species for
 * Fishery Statistics Purposes (2025 edition).
 *
 * <p>Validation fails open if the ASFIS dataset could not be loaded (i.e.
 * an unrecognised code is accepted rather than incorrectly rejected).
 *
 * @see AsfisData
 */
public final class AquaticSpeciesValidator implements ContentInterface {

    /** The singleton instance. */
    public static final AquaticSpeciesValidator INSTANCE = new AquaticSpeciesValidator();

    /** Creates a new {@link AquaticSpeciesValidator}. */
    public AquaticSpeciesValidator() {}

    static final String ERR_CODE_UNKNOWN = "GE-C121";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        // Fail open if dataset is unavailable
        if (AsfisData.SPECIES.isEmpty()) return List.of();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String code = components.get(0).getValue();
        if (code == null || code.isEmpty()) return List.of();

        if (!AsfisData.SPECIES.containsKey(code)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_UNKNOWN, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
