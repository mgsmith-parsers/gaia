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
 * Custom semantic validator for AI 401 — Global Identification Number for
 * Consignment (GINC).
 *
 * <p>The standard pipeline already verifies that the value is 1–30 characters
 * drawn from the GS1 AI encodable character set. This validator applies the
 * additional semantic rule required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GINC must begin with a recognised GS1 company prefix. The GS1
 *       company prefix is the numeric portion at the start of the identifier;
 *       a value whose leading characters are not a known numeric prefix is not
 *       a validly constructed GINC.</li>
 * </ul>
 *
 * <h2>GINC structure</h2>
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Consignment Reference (alphanumeric) ]
 *   |&lt;-- GS1 prefix check starts at index 0 --&gt;|
 * </pre>
 *
 * <p>Note: there is no check digit on a GINC. The registry lookup handles
 * non-numeric leading characters correctly — all prefix ranges are numeric,
 * so any value starting with a letter will fail the lookup.
 */
public final class GINCValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GINCValidator INSTANCE = new GINCValidator();

    /** Creates a new {@link GINCValidator}. */
    public GINCValidator() {}

    static final String ERR_CODE_PREFIX = "GE-C133";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full GINC value (GS1 Company Prefix + Consignment Reference)
        String ginc = components.get(0).getValue();

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(ginc)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
