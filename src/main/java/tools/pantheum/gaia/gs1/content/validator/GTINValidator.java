package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.util.StringUtils;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for GTIN-bearing Application Identifiers
 * (AIs 01 and 02).
 *
 * <p>The standard pipeline already verifies that the value is exactly 14 digits
 * and that the modulo-10 check digit (last digit) is correct. This validator
 * applies the additional semantic rules required by the GS1 General Specifications:
 *
 * <ul>
 *   <li>The GTIN must not be all zeros.</li>
 *   <li>The native GTIN type is determined from the number of leading zeros, and
 *       the GS1 company prefix is extracted at the correct offset before being
 *       checked against the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>Embedded GTIN type detection</h2>
 * <p>All GTINs are stored as 14 digits. The number of leading zeros reveals the
 * original format and determines where the GS1 company prefix begins:
 * <pre>
 *   000000XXXXXXXX  →  GTIN-8   (6 leading zeros, prefix at index 6)
 *   00XXXXXXXXXXXX  →  GTIN-12  (2 leading zeros, prefix at index 2)
 *   0XXXXXXXXXXXXX  →  GTIN-13  (1 leading zero,  prefix at index 1)
 *   1–9XXXXXXXXXXX  →  GTIN-14  (indicator digit,  prefix at index 1)
 * </pre>
 *
 * <p>AI 01 carries the GTIN of the trade item itself; AI 02 carries the GTIN of
 * contained trade items (variable-measure). Both share the same 14-digit GTIN
 * structure and validation rules.
 */
public final class GTINValidator implements ContentInterface {

    /** The singleton instance. */
    public static final GTINValidator INSTANCE = new GTINValidator();

    /** Creates a new {@link GTINValidator}. */
    public GTINValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C145";
    static final String ERR_CODE_PREFIX    = "GE-C146";

    // -------------------------------------------------------------------------
    // Shared static helpers — used by GTINEnricher
    // -------------------------------------------------------------------------

    /**
     * Returns the human-readable packaging level label for a GTIN-14 indicator
     * digit, or {@code null} if not applicable (indicator {@code 0} means the
     * GTIN is a padded GTIN-8/12/13, not a packaging level indicator).
     *
     * @param indicatorDigit single character: {@code '1'}–{@code '9'}
     * @return a new {@code String}
     */
    public static String packagingLevel(char indicatorDigit) {
        if (indicatorDigit == '9') return "Variable measure trade item";
        if (indicatorDigit >= '1' && indicatorDigit <= '8')
            return "Logistic unit (level " + indicatorDigit + ")";
        return null; // '0' = padded GTIN — no packaging level
    }

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the full 14-digit GTIN value
        String value = components.get(0).getValue();

        if (StringUtils.isAllZeros(value)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // Strip the leading-zero padding (or, for GTIN-14, the indicator digit)
        // so the GS1 company prefix is checked at the correct offset for the
        // native GTIN type. Note this differs from GS1Utils.nativeValue, which
        // preserves the full 14 digits for GTIN-14.
        String prefixValue;
        if (value.startsWith(GS1Constants.GTIN_8_PADDING)) {
            prefixValue = value.substring(GS1Constants.GTIN_8_PREFIX_OFFSET);
        } else if (value.startsWith(GS1Constants.GTIN_12_PADDING)) {
            prefixValue = value.substring(GS1Constants.GTIN_12_PREFIX_OFFSET);
        } else {
            // GTIN-13 (1 leading zero) or GTIN-14 (indicator digit) — strip one
            prefixValue = value.substring(GS1Constants.GTIN_13_14_PREFIX_OFFSET);
        }

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(prefixValue)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
