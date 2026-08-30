package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.ImeiRbiData;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8040 (IMEI) and AI 8041 (IMEI2) by decomposing the 15-digit
 * International Mobile Equipment Identity into its constituent fields.
 *
 * <p>An IMEI is structured as:
 * <pre>
 *   [ Type Allocation Code (8) ][ Serial number (6) ][ Luhn check digit (1) ]
 *     |&lt;- RBI (2) -&gt;|
 * </pre>
 *
 * <p>The leading 2 digits of the TAC are the Reporting Body Identifier (RBI), naming
 * the GSMA-appointed body that allocated the TAC — e.g. {@code 35} (TÜV SÜD BABT) or
 * {@code 01} (CTIA/PTCRB).
 *
 * <h2>Standard display format</h2>
 * {@code AA-BBBBBB-CCCCCC-D}, e.g. {@code "49-015420-323751-8"} — the GSMA grouping,
 * which splits the TAC at the RBI boundary. (The legacy {@code 6-2-6-1} grouping cuts
 * the TAC where the discontinued Final Assembly Code used to begin and is not emitted.)
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code IMEI_RBI} — the 2-digit Reporting Body Identifier leading the TAC,
 *       e.g. {@code "49"}</li>
 *   <li>{@code IMEI_TAC} — the 8-digit Type Allocation Code identifying the model,
 *       e.g. {@code "49015420"}</li>
 *   <li>{@code IMEI_SERIAL} — the 6-digit serial number within that TAC,
 *       e.g. {@code "323751"}</li>
 *   <li>{@code IMEI_CHECK_DIGIT} — the final Luhn check digit, e.g. {@code "8"}</li>
 *   <li>{@code IMEI_FORMATTED} — the standard dash-formatted IMEI,
 *       e.g. {@code "49-015420-323751-8"}</li>
 *   <li>{@code IMEI_RBI_NAME} — the reporting body's name when the RBI appears in
 *       {@link ImeiRbiData}, e.g. {@code "TÜV SÜD BABT (United Kingdom)"}; omitted
 *       otherwise</li>
 * </ul>
 *
 * <p>Decomposition is purely positional and does not re-validate the check digit;
 * {@code ImeiValidator} owns that. {@code IMEI_RBI_NAME} resolves against a partial,
 * locally-held table, so its absence means the code is not listed — never that the IMEI
 * is invalid — and no validation outcome may be derived from it. If the base value is
 * not exactly 15 digits the enricher returns an empty list rather than a partial
 * decomposition, so a value that failed content validation is not misleadingly split.
 *
 * <p>The same layout governs both AIs, so a single instance enriches 8040 and 8041.
 * Stateless and thread-safe, as {@link InterpretationEnricherInterface} requires.
 */
public final class ImeiEnricher implements InterpretationEnricherInterface {

    private static final int IMEI_LENGTH = 15;
    private static final int RBI_END     = 2;   // digits 0–1   → Reporting Body Identifier (leads the TAC)
    private static final int TAC_END     = 8;   // digits 0–7   → Type Allocation Code
    private static final int SERIAL_END  = 14;  // digits 8–13  → serial number; digit 14 → check

    /** Creates a new {@link ImeiEnricher}. */
    public ImeiEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != IMEI_LENGTH || !StringUtils.isAllDigits(baseValue)) {
            return Collections.emptyList();
        }

        String rbi        = baseValue.substring(0, RBI_END);
        String tac        = baseValue.substring(0, TAC_END);
        String serial     = baseValue.substring(TAC_END, SERIAL_END);
        String checkDigit = baseValue.substring(SERIAL_END);

        // Standard GSMA display: AA-BBBBBB-CCCCCC-D (the TAC split at the RBI boundary)
        String formatted = rbi + "-" + tac.substring(RBI_END) + "-" + serial + "-" + checkDigit;

        List<GS1AIInterpretation> results = new ArrayList<>(6);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_RBI, null, rbi));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_TAC, null, tac));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_SERIAL, null, serial));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_CHECK_DIGIT, null, checkDigit));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_FORMATTED, null, formatted));

        // Appended last, and only when the RBI is one this build knows; an unlisted code
        // is ordinary, so the interpretation is simply absent rather than a placeholder.
        ImeiRbiData.nameForCode(rbi).ifPresent(bodyName ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.IMEI_RBI_NAME, null, bodyName)));

        return results;
    }
}
