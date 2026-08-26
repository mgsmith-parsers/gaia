package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.util.LuhnUtils;
import tools.pantheum.gaia.gs1.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 8040 (IMEI) and AI 8041 (IMEI2) — the
 * International Mobile Equipment Identity of a cellular device.
 *
 * <p>The standard pipeline already verifies that the value is exactly 15 numeric
 * digits ({@code N15}). This validator applies the additional rule the GS1
 * General Specifications require for these AIs: the 15-digit IMEI must satisfy the
 * <strong>Luhn (modulo&nbsp;10) check</strong>, where the final digit is a check
 * digit computed over the preceding 14.
 *
 * <h2>IMEI structure</h2>
 * <pre>
 *   [ Type Allocation Code (8) ][ Serial number (6) ][ Luhn check digit (1) ]
 *   |&lt;----------------------- 15 digits ----------------------------------&gt;|
 * </pre>
 *
 * <p><strong>Not a GS1 check digit.</strong> The IMEI uses the Luhn algorithm
 * (double every second digit from the right), which is distinct from the GS1
 * modulo-10 check digit ({@code GS1Utils.calculateCheckDigit}, weights 3-1-3-1…)
 * applied to keys such as GTIN and SSCC. The two are not interchangeable, so this
 * validator uses {@link tools.pantheum.gaia.gs1.util.LuhnUtils} rather than the
 * GS1 check-digit routine.
 *
 * <p>The same rule governs both AIs, so a single instance validates 8040 and 8041.
 * Stateless and thread-safe.
 */
public final class ImeiValidator implements ContentInterface {

    public static final ImeiValidator INSTANCE = new ImeiValidator();

    /** The exact digit length of an IMEI as declared by AIs 8040 / 8041 ({@code N15}). */
    static final int IMEI_LENGTH = 15;

    /** Raised when the IMEI's Luhn check digit does not verify. */
    static final String ERR_CODE_CHECK_DIGIT = "GE-C169";

    public ImeiValidator() {}

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String imei = components.get(0).getValue();

        // The format stage (N15) guarantees 15 digits by the time this runs; guard defensively
        // so a caller invoking the validator out of pipeline order cannot trip the Luhn routine.
        if (imei == null || imei.length() != IMEI_LENGTH || !StringUtils.isAllDigits(imei)) {
            return List.of();
        }

        if (!LuhnUtils.isValid(imei)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_CHECK_DIGIT, element.getAi(),
                    element.getPosition(),
                    Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
