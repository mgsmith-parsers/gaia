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
 * Custom semantic validator for AI 255 — Global Coupon Number (GCN).
 *
 * <p>The standard pipeline already verifies that:
 * <ul>
 *   <li>The first 13 characters are digits with a valid modulo-10 check digit.</li>
 *   <li>The optional serial suffix (component 2, up to 12 digits) is numeric.</li>
 * </ul>
 *
 * <p>This validator applies the additional semantic rules:
 * <ul>
 *   <li>The 13-digit GCN key must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at index 0 of the key (no leading
 *       indicator or extension digit) — must match a recognised range in the
 *       GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>GCN structure</h2>
 * <pre>
 *   [ GS1 Company Prefix ][ Coupon Reference ][ Check digit (1) ][ Serial (0–12 digits, optional) ]
 *   |&lt;-- GS1 prefix check starts at index 0 ----------------------------&gt;||
 * </pre>
 */
public final class GCNValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GCNValidator INSTANCE = new GCNValidator();

    /** Creates a new {@link GCNValidator}. */
    public GCNValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C128";
    static final String ERR_CODE_PREFIX    = "GE-C129";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: 13-digit GCN key (GS1 Company Prefix + Coupon Reference + Check digit)
        String gcnKey = components.get(0).getValue();

        if (StringUtils.isAllZeros(gcnKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 — no leading indicator or extension digit.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gcnKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
