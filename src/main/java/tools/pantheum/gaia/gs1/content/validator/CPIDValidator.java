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
 * Custom semantic validator for AI 8010 — Component/Part Identifier (CPID).
 *
 * <p>The standard pipeline already verifies that the value is 5–30 characters
 * drawn from GS1 character set Y ({@code A–Z}, {@code 0–9}, {@code #},
 * {@code /}, {@code -}). This validator applies the additional semantic rule
 * required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The CPID must begin with a recognised GS1 company prefix. The GS1
 *       company prefix is the numeric portion at the start of the identifier;
 *       a value whose leading characters are not a known numeric prefix is not
 *       a validly constructed CPID.</li>
 * </ul>
 *
 * <h2>CPID structure</h2>
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Component/Part Reference (Y charset) ]
 *   |&lt;-- GS1 prefix check starts at index 0 --&gt;|
 * </pre>
 *
 * <p>Note: there is no check digit on a CPID. All prefix ranges in the registry
 * are numeric, so a CPID starting with a non-digit character will naturally
 * fail the lookup.
 */
public final class CPIDValidator implements ContentInterface {

    public static final CPIDValidator INSTANCE = new CPIDValidator();

    public CPIDValidator() {}

    static final String ERR_CODE_PREFIX = "GE-C122";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full CPID value (GS1 Company Prefix + Component/Part Reference)
        String cpid = components.get(0).getValue();

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(cpid)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
