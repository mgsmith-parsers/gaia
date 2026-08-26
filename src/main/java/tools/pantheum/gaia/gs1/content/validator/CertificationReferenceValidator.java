package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for Certification Reference Application Identifiers
 * (AIs 7230–7239).
 *
 * <p>The fourth digit of the AI (0–9) is a sequence number that allows multiple
 * certification references to be encoded in a single element string.
 *
 * <p>The standard pipeline already verifies:
 * <ul>
 *   <li>Component 0 (X2): exactly 2 GS1 AI encodable characters — the
 *       certification scheme code.</li>
 *   <li>Component 1 (X..28): 1–28 GS1 AI encodable characters — the
 *       certification reference number.</li>
 * </ul>
 *
 * <p>This validator applies additional semantic rules:
 * <ul>
 *   <li>The certification scheme code (component 0) must be one of the
 *       GS1-defined allowed values (see {@link GS1Constants#CERT_SCHEME_NAMES}).</li>
 *   <li>The certification reference (component 1) must not be blank.</li>
 * </ul>
 *
 * <h2>Structure</h2>
 * <pre>
 *   [ Certification scheme code (2 chars) ][ Certification reference (1–28 chars) ]
 *   Component 0                              Component 1
 * </pre>
 */
public final class CertificationReferenceValidator implements ContentInterface {

    public static final CertificationReferenceValidator INSTANCE = new CertificationReferenceValidator();

    public CertificationReferenceValidator() {}

    static final String ERR_CODE_SCHEME          = "GE-C123";
    static final String ERR_CODE_REFERENCE_BLANK = "GE-C124";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 2) return List.of();

        // Component 0: GS1-defined certification scheme code
        String schemeCode = components.get(0).getValue();
        // Component 1: certification reference number
        String reference  = components.get(1).getValue();

        if (!GS1Constants.CERT_SCHEME_NAMES.containsKey(schemeCode)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_SCHEME, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        if (reference.isBlank()) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_REFERENCE_BLANK, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
