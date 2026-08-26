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
 * Custom semantic validator for AI 8013 — Global Model Number (GMN).
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
 * <h2>GMN structure</h2>
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Model Reference (alphanumeric) ][ Check pair (2 chars) ]
 *   |&lt;-------- data -----------------------------------------------&gt;||&lt;-- last 2 chars --&gt;|
 * </pre>
 *
 * <h2>Example (from GS1 General Specifications §7.9.5)</h2>
 * <pre>
 *   GMN value: 1987654Ad4X4bL5ttr2310c2K
 *   Data:      1987654Ad4X4bL5ttr2310c   (23 chars)
 *   Check pair: 2K
 * </pre>
 */
public final class GMNValidator implements ContentInterface {

    public static final GMNValidator INSTANCE = new GMNValidator();

    /** Minimum GMN length: at least 1 data character + 2 check characters. */
    private static final int MIN_LENGTH = 3;

    public GMNValidator() {}

    static final String ERR_CODE_TOO_SHORT  = "GE-C136";
    static final String ERR_CODE_PREFIX     = "GE-C137";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full GMN value (data + 2-character check pair)
        String gmn = components.get(0).getValue();

        if (gmn.length() < MIN_LENGTH) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_TOO_SHORT, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // GS1 prefix starts at index 0 — the company prefix is the numeric portion
        // at the beginning of the GMN, before the model reference and check pair.
        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(gmn)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
