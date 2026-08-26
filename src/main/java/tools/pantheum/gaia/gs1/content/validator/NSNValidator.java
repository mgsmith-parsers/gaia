package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.util.StringUtils;
import tools.pantheum.gaia.gs1.dataset.NCBData;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7001 — NATO Stock Number (NSN).
 *
 * <p>The standard pipeline verifies that the value is exactly 13 digits.
 * There is no check digit on an NSN. This validator applies structural and
 * semantic rules based on the NSN format defined in STANAG 3150.
 *
 * <h2>NSN structure (13 digits)</h2>
 * <pre>
 *   [ NSCG (4) ][ NCB code (2) ][ Item number (7) ]
 *    Digits 1–4    Digits 5–6      Digits 7–13
 *
 *   NSCG = Federal Supply Group (2) + class within that group (2)
 * </pre>
 * Digits 5–13 together form the National Item Identification Number (NIIN).
 *
 * <h2>Validation rules</h2>
 * <ul>
 *   <li>The NSN must not be all zeros. Note this is a strict subset of the
 *       NSCG rule below, so it only reports separately because it is checked
 *       first — the order of the two is deliberate.</li>
 *   <li>The NSCG (digits 1–4) must not be {@code 0000}. The Federal Supply
 *       Group and class within it are not otherwise validated.</li>
 *   <li>The NCB code (digits 5–6) must be a cataloguing nation code assigned
 *       in the NSPA AC/135 list — see {@link NCBData}.</li>
 *   <li>The item number (digits 7–13) must not be all zeros.</li>
 * </ul>
 * At most one error is reported: each rule returns as soon as it fails, so the
 * returned list is never an exhaustive account of what is wrong with a value.
 */
public final class NSNValidator implements ContentInterface {

    public static final NSNValidator INSTANCE = new NSNValidator();

    // -------------------------------------------------------------------------
    // NSN segment offsets
    // -------------------------------------------------------------------------

    private static final int NSCG_START         = 0;
    private static final int NSCG_END           = 4;
    private static final int COUNTRY_CODE_START = 4;
    private static final int COUNTRY_CODE_END   = 6;
    private static final int ITEM_NUMBER_START  = 6;
    private static final int ITEM_NUMBER_END    = 13;

    public NSNValidator() {}

    static final String ERR_CODE_ALL_ZEROS        = "GE-C154";
    static final String ERR_CODE_NSCG             = "GE-C155";
    static final String ERR_CODE_NCB_COUNTRY_CODE = "GE-C156";
    static final String ERR_CODE_ITEM_ALL_ZEROS   = "GE-C157";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String nsn = components.get(0).getValue();
        // Length is already enforced by the standard pipeline; the null check guards
        // direct callers that build an element themselves rather than parsing one.
        if (nsn == null || nsn.length() != 13) return List.of();

        String nscg        = nsn.substring(NSCG_START,         NSCG_END);
        String countryCode = nsn.substring(COUNTRY_CODE_START, COUNTRY_CODE_END);
        String itemNumber  = nsn.substring(ITEM_NUMBER_START,  ITEM_NUMBER_END);

        // Full NSN must not be all zeros. Must stay ahead of the NSCG check below,
        // which would otherwise absorb this case and report GE-C155 instead.
        if (StringUtils.isAllZeros(nsn)) {
            return error(ERR_CODE_ALL_ZEROS, element, language);
        }

        // NSCG must not be 0000
        if (StringUtils.isAllZeros(nscg)) {
            return error(ERR_CODE_NSCG, element, language);
        }

        // NCB code must be a cataloguing nation code assigned in the NSPA AC/135 list
        if (!NCBData.COUNTRY_CODES.containsKey(countryCode)) {
            return error(ERR_CODE_NCB_COUNTRY_CODE, element, language);
        }

        // Item number must not be all zeros
        if (StringUtils.isAllZeros(itemNumber)) {
            return error(ERR_CODE_ITEM_ALL_ZEROS, element, language);
        }

        return List.of();
    }

    /** Builds the single-error result for {@code errorId} against {@code element}. */
    private static List<GaiaError> error(String errorId, GS1AIObjectElement element,
                                        GaiaConstants.Language language) {
        // Map.of rejects null keys and values, so both message parameters are coalesced.
        String ai    = element.getAi()    != null ? element.getAi()    : "";
        String value = element.getValue() != null ? element.getValue() : "";

        return List.of(ErrorRegistry.INSTANCE.create(
                errorId, element.getAi(), element.getPosition(), Map.of("ai", ai, "value", value), language));
    }
}
