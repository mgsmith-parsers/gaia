package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.util.StringUtils;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 8003 — Global Returnable Asset Identifier (GRAI).
 *
 * <p>The standard pipeline already verifies:
 * <ul>
 *   <li>Component 0 (N1): must be {@code "0"} — validated by the {@code zero} format validator.</li>
 *   <li>Component 1 (N13): digits only, with a valid modulo-10 check digit.</li>
 *   <li>Component 2 (X..16): optional serial — GS1 AI encodable characters only.</li>
 * </ul>
 *
 * <p>This validator applies the additional semantic rules:
 * <ul>
 *   <li>The 13-digit GRAI key (component 1) must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at index 0 of the 13-digit key
 *       (no leading indicator or extension digit) — must match a recognised
 *       range in the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>GRAI structure</h2>
 * <pre>
 *   [ "0" (1 digit) ][ GS1 Company Prefix ][ Asset Type ][ Check digit (1) ][ Serial (0–16, optional) ]
 *   Component 0        Component 1 — GS1 prefix check starts at index 0      Component 2
 * </pre>
 */
public final class GRAIValidator implements ContentInterface {

    public static final GRAIValidator INSTANCE = new GRAIValidator();

    public GRAIValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C139";
    static final String ERR_CODE_PREFIX    = "GE-C140";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 2) return List.of();

        // Component 1: 13-digit GRAI key (GS1 Company Prefix + Asset Type + Check digit)
        String graiKey = components.get(1).getValue();

        if (StringUtils.isAllZeros(graiKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 of the key — no leading indicator or extension digit.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(graiKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
