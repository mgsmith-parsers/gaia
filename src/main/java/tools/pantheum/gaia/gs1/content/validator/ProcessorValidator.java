package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for Processor Number Application Identifiers
 * (AIs 7030–7039).
 *
 * <p>The fourth digit of the AI (0–9) is a sequence number that allows up to
 * ten processor references to be encoded in a single element string.
 *
 * <p>The standard pipeline already verifies:
 * <ul>
 *   <li>Component 0 (N3): exactly 3 digits.</li>
 *   <li>Component 1 (X..27): 1–27 GS1 AI encodable characters.</li>
 * </ul>
 *
 * <p>This validator adds the semantic check that the 3-digit country code
 * (component 0) is a valid ISO 3166-1 numeric country code. The component
 * definition carries no {@code format} hint, so the standard
 * {@link tools.pantheum.gaia.gs1.content.ComponentValidator} does not
 * perform this check — this custom validator fills that gap.
 *
 * <h2>Component structure</h2>
 * <pre>
 *   [ ISO 3166-1 numeric country code (3 digits) ][ Processor identifier (1–27 chars) ]
 *   Component 0                                     Component 1
 * </pre>
 */
public final class ProcessorValidator implements ContentInterface {

    public static final ProcessorValidator INSTANCE = new ProcessorValidator();

    public ProcessorValidator() {}

    static final String ERR_CODE_COUNTRY_CODE = "GE-C158";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 2) return List.of();

        // Component 0: ISO 3166-1 numeric country code
        String countryCode = components.get(0).getValue();

        // Fail open if the ISO 3166 data is unavailable
        if (Iso3166Data.NUMERIC_TO_NAME.isEmpty()) return List.of();

        if (!Iso3166Data.NUMERIC_TO_NAME.containsKey(countryCode)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_COUNTRY_CODE, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
