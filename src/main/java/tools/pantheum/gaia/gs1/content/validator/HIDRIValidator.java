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
 * Custom semantic validator for AI 8014 — Highly Individualised Device
 * Registration Identifier (HIDRI).
 *
 * <h2>Validation</h2>
 * <p>The standard pipeline already verifies that the value is 1–25 characters
 * drawn from the GS1 AI encodable character set (CSET82), and
 * {@link tools.pantheum.gaia.gs1.content.CheckDigitCharacterValidator} verifies the
 * MOD 1021,32 check character pair flagged by the component's
 * {@code checkCharacters} attribute (failing with {@code GE-C004}). This
 * validator additionally verifies:
 * <ul>
 *   <li>The value is long enough to contain at least one data character plus
 *       the two-character check pair (minimum 3 characters total).</li>
 *   <li>The value begins with a recognised GS1 company prefix.</li>
 * </ul>
 *
 * <h2>HIDRI structure</h2>
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Device Reference (alphanumeric) ][ Check pair (2 chars) ]
 *   |&lt;-- GS1 prefix check starts at index 0 -----------------------------------&gt;||
 * </pre>
 *
 * <p>Note: AI 8014 requires AI 01 (GTIN) to also be present in the element string.
 */
public final class HIDRIValidator implements ContentInterface {

    public static final HIDRIValidator INSTANCE = new HIDRIValidator();

    /** Minimum HIDRI length: at least 1 data character + 2 check characters. */
    private static final int MIN_LENGTH = 3;

    public HIDRIValidator() {}

    static final String ERR_CODE_TOO_SHORT  = "GE-C147";
    static final String ERR_CODE_PREFIX     = "GE-C148";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full HIDRI value (data + 2-character check pair)
        String hidri = components.get(0).getValue();

        if (hidri.length() < MIN_LENGTH) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_TOO_SHORT, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(hidri)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
