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
 * Custom semantic validator for AI 402 — Global Shipment Identification Number (GSIN).
 *
 * <p>The standard pipeline already verifies that the value is exactly 17 digits
 * and that the modulo-10 check digit (last digit) is correct. This validator
 * applies the additional semantic rules required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GSIN must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at index 0 of the value (no
 *       leading indicator or extension digit) — must match a recognised range
 *       in the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>GSIN structure</h2>
 * <pre>
 *   [ GS1 Company Prefix ][ Shipper's Reference ][ Check digit (1) ]
 *   |&lt;-- GS1 prefix check starts at index 0 ----------------------&gt;|
 * </pre>
 */
public final class GSINValidator implements ContentInterface {

    public static final GSINValidator INSTANCE = new GSINValidator();

    public GSINValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C141";
    static final String ERR_CODE_PREFIX    = "GE-C142";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full 17-digit GSIN value
        String gsin = components.get(0).getValue();

        if (StringUtils.isAllZeros(gsin)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 — no leading indicator or extension digit.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gsin)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
