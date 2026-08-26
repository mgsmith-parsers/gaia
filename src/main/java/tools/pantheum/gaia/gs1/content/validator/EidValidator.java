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
 * Custom semantic validator for AI 8042 (ESIM) — the eUICC Identifier (EID) of an
 * embedded SIM.
 *
 * <p>The standard pipeline already verifies that the value is exactly 32 numeric
 * digits ({@code N32}). This validator applies the additional rule the GSMA eUICC
 * specifications require: the 32nd digit is a <strong>Luhn (modulo&nbsp;10) check
 * digit</strong> computed over the preceding 31.
 *
 * <h2>EID structure</h2>
 * <pre>
 *   [ MII (2) ][ issuer / platform assignment (29) ][ Luhn check digit (1) ]
 *   |&lt;--------------------- 32 digits ---------------------------------&gt;|
 * </pre>
 *
 * <p><strong>Only the check digit is enforced.</strong> The interior fields (country
 * code, issuer identifier, platform version) are GSMA-version-dependent, so this
 * validator deliberately does not assert their boundaries or values — including the
 * conventional {@code 89} telecom MII prefix, which is left to
 * {@link tools.pantheum.gaia.gs1.interpretation.enricher.EidEnricher} to surface
 * rather than to reject on.
 *
 * <p>As with {@code ImeiValidator}, this is the Luhn algorithm
 * ({@link tools.pantheum.gaia.gs1.util.LuhnUtils}), <em>not</em> the GS1 modulo-10
 * check digit used by GTIN and SSCC; the two are not interchangeable.
 *
 * <p>Stateless and thread-safe.
 */
public final class EidValidator implements ContentInterface {

    public static final EidValidator INSTANCE = new EidValidator();

    /** The exact digit length of an EID as declared by AI 8042 ({@code N32}). */
    static final int EID_LENGTH = 32;

    /** Raised when the EID's Luhn check digit does not verify. */
    static final String ERR_CODE_CHECK_DIGIT = "GE-C170";

    public EidValidator() {}

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String eid = components.get(0).getValue();

        // The format stage (N32) guarantees 32 digits by the time this runs; guard defensively
        // so a caller invoking the validator out of pipeline order cannot trip the Luhn routine.
        if (eid == null || eid.length() != EID_LENGTH || !StringUtils.isAllDigits(eid)) {
            return List.of();
        }

        if (!LuhnUtils.isValid(eid)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_CHECK_DIGIT, element.getAi(),
                    element.getPosition(),
                    Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
