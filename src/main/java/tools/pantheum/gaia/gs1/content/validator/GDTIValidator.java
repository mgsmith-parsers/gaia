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
 * Custom semantic validator for AI 253 — Global Document Type Identifier (GDTI).
 *
 * <p>The standard pipeline already verifies that:
 * <ul>
 *   <li>The first 13 characters are digits with a valid modulo-10 check digit.</li>
 *   <li>The optional serial suffix (positions 14–30) contains only GS1 AI
 *       encodable characters.</li>
 * </ul>
 *
 * <p>This validator applies the additional semantic rules:
 * <ul>
 *   <li>The 13-digit GDTI key must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at index 0 of the value (no
 *       leading indicator or extension digit) — must match a recognised range
 *       in the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>GDTI structure</h2>
 * <pre>
 *   [ GS1 Company Prefix ][ Document Type ][ Check digit (1) ][ Serial (0–17, optional) ]
 *   |&lt;-- GS1 prefix check starts at index 0 ---------------------------&gt;||
 * </pre>
 */
public final class GDTIValidator implements ContentInterface {

    public static final GDTIValidator INSTANCE = new GDTIValidator();

    public GDTIValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C130";
    static final String ERR_CODE_PREFIX    = "GE-C131";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: 13-digit GDTI key (GS1 Company Prefix + Document Type + Check digit)
        String gdtiKey = components.get(0).getValue();

        if (StringUtils.isAllZeros(gdtiKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 — no leading indicator or extension digit.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gdtiKey)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
