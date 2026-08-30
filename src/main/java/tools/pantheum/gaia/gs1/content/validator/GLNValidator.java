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
 * Custom semantic validator for GLN-bearing Application Identifiers
 * (AIs 410–417).
 *
 * <p>The standard pipeline already verifies that the value is exactly 13 digits
 * and that the modulo-10 check digit is correct. This validator applies the
 * additional semantic rules required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GLN must not be all zeros — {@code 0000000000000} is a null
 *       placeholder and is not a valid, assignable location number.</li>
 *   <li>The leading digits of the GLN must match a recognised GS1 company
 *       prefix range in the GS1 prefix registry.</li>
 * </ul>
 */
public final class GLNValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GLNValidator INSTANCE = new GLNValidator();

    /** Creates a new {@link GLNValidator}. */
    public GLNValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C134";
    static final String ERR_CODE_PREFIX    = "GE-C135";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the 13-digit GLN key (GS1 Company Prefix + Location Reference + Check digit)
        String gln = components.get(0).getValue();

        if (StringUtils.isAllZeros(gln)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gln)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }

}
