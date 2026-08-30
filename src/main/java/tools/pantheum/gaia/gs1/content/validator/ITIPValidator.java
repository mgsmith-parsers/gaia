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
 * Custom semantic validator for AI 8006 — Individual Trade Item Piece (ITIP).
 *
 * <p>The standard pipeline already verifies:
 * <ul>
 *   <li>Component 0 (N14): 14 digits with a valid modulo-10 check digit.</li>
 *   <li>Component 1 (N4, {@code pieceoftotal}): piece number ≤ total pieces,
 *       validated by the {@code pieceoftotal} format validator.</li>
 * </ul>
 *
 * <p>This validator applies the additional semantic rules to the embedded GTIN
 * (component 0):
 * <ul>
 *   <li>The GTIN must not be all zeros.</li>
 *   <li>The native GTIN type is determined from the number of leading zeros,
 *       and the GS1 company prefix is extracted at the correct offset before
 *       being checked against the GS1 prefix registry.</li>
 * </ul>
 *
 * <h2>ITIP structure</h2>
 * <pre>
 *   [ GTIN-14 (14 digits) ][ Piece (2 digits) ][ Total (2 digits) ]
 *   Component 0              Component 1 (pieceoftotal)
 * </pre>
 *
 * <h2>GTIN type detection (component 0)</h2>
 * <pre>
 *   000000XXXXXXXX  →  GTIN-8   (6 leading zeros, prefix at index 6)
 *   00XXXXXXXXXXXX  →  GTIN-12  (2 leading zeros, prefix at index 2)
 *   0XXXXXXXXXXXXX  →  GTIN-13  (1 leading zero,  prefix at index 1)
 *   1–9XXXXXXXXXXX  →  GTIN-14  (indicator digit,  prefix at index 1)
 * </pre>
 */
public final class ITIPValidator implements ContentInterface {

    /** The singleton instance. */
    public static final ITIPValidator INSTANCE = new ITIPValidator();

    /** Creates a new {@link ITIPValidator}. */
    public ITIPValidator() {}

    static final String ERR_CODE_ALL_ZEROS = "GE-C152";
    static final String ERR_CODE_PREFIX    = "GE-C153";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        // Component 0: the embedded 14-digit GTIN
        String gtin = components.get(0).getValue();

        if (StringUtils.isAllZeros(gtin)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ALL_ZEROS, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // Strip the leading-zero padding (or, for GTIN-14, the indicator digit)
        // so the GS1 company prefix is checked at the correct offset for the
        // native GTIN type. Note this differs from GS1Utils.nativeValue, which
        // preserves the full 14 digits for GTIN-14.
        String prefixValue;
        if (gtin.startsWith(GS1Constants.GTIN_8_PADDING)) {
            prefixValue = gtin.substring(GS1Constants.GTIN_8_PREFIX_OFFSET);
        } else if (gtin.startsWith(GS1Constants.GTIN_12_PADDING)) {
            prefixValue = gtin.substring(GS1Constants.GTIN_12_PREFIX_OFFSET);
        } else {
            // GTIN-13 (1 leading zero) or GTIN-14 (indicator digit) — strip one
            prefixValue = gtin.substring(GS1Constants.GTIN_13_14_PREFIX_OFFSET);
        }

        if (!GS1PrefixRegistry.INSTANCE.isKnownPrefix(prefixValue)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_PREFIX, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
