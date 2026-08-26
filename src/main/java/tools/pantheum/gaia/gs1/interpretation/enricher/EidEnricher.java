package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.Iso7812Data;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8042 (ESIM) by decomposing the 32-digit eUICC Identifier (EID) of an
 * embedded SIM.
 *
 * <p>An EID is structured as:
 * <pre>
 *   [ MII (2) ][ body (29) ][ Luhn check digit (1) ]
 * </pre>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code SIM_MII} — the 2-digit Major Industry Identifier, {@code "89"} for
 *       telecommunications (ITU-T E.118)</li>
 *   <li>{@code EID_BODY} — the 29 digits between the MII and the check digit</li>
 *   <li>{@code EID_CHECK_DIGIT} — the final Luhn check digit</li>
 *   <li>{@code SIM_MII_NAME} — the ISO/IEC 7812-1 issuer-industry category of the MII,
 *       i.e. {@code "Healthcare, telecommunications and other future industry
 *       assignments"}</li>
 * </ul>
 *
 * <p>ISO/IEC 7812 defines the MII as the identifier's <strong>first digit</strong>; the
 * {@code 89} pair quoted here is the ITU-T E.118 telecommunications assignment, whose
 * leading {@code 8} is the MII proper. {@code SIM_MII_NAME} is resolved from that leading
 * digit through {@link Iso7812Data}, so for a well-formed EID it is constant — reported
 * for traceability to the standard rather than as a discriminator.
 *
 * <p><strong>Why the body is not decomposed further.</strong> The GSMA specifications
 * subdivide the interior into country code, issuer identifier and platform version,
 * but those boundaries are specification-version dependent and cannot be determined
 * from the value alone. Splitting them here would present a guess as a fact, so the
 * body is surfaced whole.
 *
 * <p>Decomposition is purely positional and does not re-validate the check digit;
 * {@code EidValidator} owns that. A value that is not exactly 32 digits yields an
 * empty list rather than a partial decomposition.
 *
 * <p>Stateless and thread-safe, as {@link InterpretationEnricherInterface} requires.
 */
public final class EidEnricher implements InterpretationEnricherInterface {

    private static final int EID_LENGTH = 32;
    private static final int MII_END    = 2;   // digits 0–1   → Major Industry Identifier
    private static final int BODY_END   = 31;  // digits 2–30  → body; digit 31 → check digit

    public EidEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != EID_LENGTH || !StringUtils.isAllDigits(baseValue)) {
            return Collections.emptyList();
        }

        String mii        = baseValue.substring(0, MII_END);
        String body       = baseValue.substring(MII_END, BODY_END);
        String checkDigit = baseValue.substring(BODY_END);

        List<GS1AIInterpretation> results = new ArrayList<>(4);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SIM_MII, null, mii));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.EID_BODY, null, body));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.EID_CHECK_DIGIT, null, checkDigit));

        // Appended last, mirroring IMEI_RBI_NAME. ISO/IEC 7812 defines the MII as the
        // leading digit, so the category comes from that rather than the 2-digit prefix.
        Iso7812Data.nameForIdentifier(mii).ifPresent(category ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.SIM_MII_NAME, null, category)));

        return results;
    }
}
