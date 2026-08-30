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
 * Custom semantic validator for GSRN-bearing Application Identifiers:
 *
 * <ul>
 *   <li>AI 8017 — GSRN (provider of services)</li>
 *   <li>AI 8018 — GSRN (recipient of services)</li>
 * </ul>
 *
 * <p>The standard pipeline already verifies that the value is exactly 18 digits
 * and that the modulo-10 check digit (last digit) is correct. This validator
 * applies the additional semantic rules required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GSRN must not be all zeros.</li>
 *   <li>The GS1 company prefix — which begins at index 0 of the value (no
 *       leading indicator or extension digit) — must match a recognised range
 *       in the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>GSRN structure</h2>
 * <pre>
 *   [ GS1 Company Prefix ][ Service Reference ][ Check digit (1) ]  =  18 digits total
 *   |&lt;-- GS1 prefix check starts at index 0 --------------------------&gt;|
 * </pre>
 *
 * <p>AIs 8017 and 8018 are mutually exclusive (they cannot appear in the same
 * element string together). Both share identical format and validation rules.
 */
public final class GSRNValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GSRNValidator INSTANCE = new GSRNValidator();

    /** Creates a new {@link GSRNValidator}. */
    public GSRNValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C143";
    static final String ERR_CODE_PREFIX    = "GE-C144";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full 18-digit GSRN value
        String gsrn = components.get(0).getValue();

        if (StringUtils.isAllZeros(gsrn)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 — no leading indicator or extension digit.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gsrn)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
