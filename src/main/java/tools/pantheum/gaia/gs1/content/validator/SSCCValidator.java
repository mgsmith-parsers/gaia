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
 * Custom semantic validator for AI 00 — Serial Shipping Container Code (SSCC).
 *
 * <p>The standard pipeline already verifies that the value is exactly 18 digits
 * and that the modulo-10 check digit (last digit) is correct. This validator
 * applies the additional semantic rules required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The SSCC must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at digit 2 (index 1), immediately
 *       after the single-digit extension digit — must match a recognised range in
 *       the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>SSCC structure</h2>
 * <pre>
 *   [ Extension digit (1) ][ GS1 Company Prefix (N) ][ Serial Reference ][ Check digit (1) ]
 *   |&lt;----- 1 digit -----&gt;||&lt;-- starts at index 1 --------------------------------&gt;||
 * </pre>
 */
public final class SSCCValidator implements ContentInterface {

    public static final SSCCValidator INSTANCE = new SSCCValidator();

    public SSCCValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C164";
    static final String ERR_CODE_PREFIX    = "GE-C165";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full 18-digit SSCC value
        String sscc = components.get(0).getValue();

        if (StringUtils.isAllZeros(sscc)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 1 — skip the leading extension digit.
        String withoutExtension = sscc.substring(1);
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(withoutExtension)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }

}
