package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7040 — GS1 UIC with Extension 1 and
 * Importer Index (UIC+EXT).
 *
 * <p>AI 7040 encodes the Unique Identification Code (UIC) of an EU 2018/574
 * ID Issuer, the National Authority that appointed it (via the GS1 UIC
 * Extension 1), and, if applicable, the Importer Index. Its use is restricted
 * to application standard 2.1.14 (European Regulation 2018/574, traceability
 * of tobacco products).
 *
 * <h2>Component structure</h2>
 * <pre>
 *   [ UIC digit 1 (N1) ][ UIC char 2 (X1) ][ Extension 1 (X1) ][ Importer Index (X1) ]
 *   Component 0          Component 1         Component 2          Component 3
 * </pre>
 *
 * <h2>Standard pipeline coverage</h2>
 * <ul>
 *   <li>Component 0 (N1): validated as a single digit by the syntax parser.</li>
 *   <li>Component 1 (X1): validated as one GS1 CSET82 character by the charset
 *       checker, but CSET82 is broader than required — see below.</li>
 *   <li>Component 2 (X1): same as component 1.</li>
 *   <li>Component 3 (X1, {@code importeridx}): validated by
 *       {@link tools.pantheum.gaia.gs1.content.componentformat.ImporterIdxValidator}.</li>
 * </ul>
 *
 * <h2>Additional semantic rules applied here</h2>
 * <p>Components 1 and 2 must each be one <em>alphanumeric</em> character from
 * the ISO/IEC 646 invariant character set (GS1 General Specifications Table 7-20).
 * This means only:
 * <ul>
 *   <li>Digits {@code 0–9}</li>
 *   <li>Uppercase letters {@code A–Z}</li>
 * </ul>
 * Lowercase letters, punctuation, and other CSET82 characters that are not
 * in the ISO/IEC 646 invariant alphanumeric subset are rejected.
 */
public final class UICValidator implements ContentInterface {

    /** The singleton instance. */
    public static final UICValidator INSTANCE = new UICValidator();

    /** Creates a new {@link UICValidator}. */
    public UICValidator() {}

    static final String ERR_CODE_SECOND_CHAR     = "GE-C166";
    static final String ERR_CODE_EXTENSION1_CHAR = "GE-C167";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 3) return List.of();

        // Component 1: second character of UIC — must be ISO/IEC 646 invariant alphanumeric
        String uicChar2 = components.get(1).getValue();
        if (!isInvariantAlphanumeric(uicChar2.charAt(0))) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_SECOND_CHAR, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // Component 2: GS1 UIC Extension 1 — must be ISO/IEC 646 invariant alphanumeric
        String extension1 = components.get(2).getValue();
        if (!isInvariantAlphanumeric(extension1.charAt(0))) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_EXTENSION1_CHAR, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }

    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if {@code c} is in the alphanumeric subset of the
     * ISO/IEC 646 invariant character set: digits {@code 0–9} and uppercase
     * letters {@code A–Z}.
     */
    private static boolean isInvariantAlphanumeric(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z');
    }
}
