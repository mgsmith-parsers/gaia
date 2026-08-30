package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 8004 — Global Individual Asset Identifier (GIAI).
 *
 * <p>The standard pipeline already verifies that the value is 1–30 characters
 * drawn from the GS1 AI encodable character set. This validator applies the
 * additional semantic rule required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GIAI must begin with a recognised GS1 company prefix. The GS1
 *       company prefix is the numeric portion at the start of the identifier;
 *       a value whose leading characters are not a known numeric prefix is not
 *       a validly constructed GIAI.</li>
 * </ul>
 *
 * <h2>GIAI structure</h2>
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Individual Asset Reference (alphanumeric) ]
 *   |&lt;-- GS1 prefix check starts at index 0 --&gt;|
 * </pre>
 *
 * <p>Note: there is no check digit on a GIAI. All prefix ranges in the registry
 * are numeric, so a GIAI starting with a letter will naturally fail the lookup.
 */
public final class GIAIValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GIAIValidator INSTANCE = new GIAIValidator();

    /** Creates a new {@link GIAIValidator}. */
    public GIAIValidator() {}

    static final String ERR_CODE_PREFIX = "GE-C132";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full GIAI value (GS1 Company Prefix + Individual Asset Reference)
        String giai = components.get(0).getValue();

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(giai)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
