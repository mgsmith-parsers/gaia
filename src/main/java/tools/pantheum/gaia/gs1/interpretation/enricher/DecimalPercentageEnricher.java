package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a 4-digit numeric percentage value with a human-readable decimal
 * representation and a percentage format label, using the last digit of the
 * AI code as the number of implied decimal places.
 *
 * <p>Used by AIs in the 394n range (PRCNT OFF — percentage discount), where
 * the 4-digit fixed-length value carries an implied decimal point determined
 * by the last digit of the AI code (0–9).
 *
 * <p>Examples:
 * <pre>
 *   AI 3940, raw "1000" → decimalPlaces=0 → "1000%"
 *   AI 3941, raw "1000" → decimalPlaces=1 → "100.0%"
 *   AI 3942, raw "1000" → decimalPlaces=2 → "10.00%"
 *   AI 3943, raw "0250" → decimalPlaces=3 → "0.250%"
 * </pre>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code DECIMAL_PERCENTAGE} — the value formatted with the implied
 *       decimal point and a {@code %} suffix, e.g. {@code "10.00%"}</li>
 *   <li>{@code PERCENTAGE_FORMAT}  — the display format, e.g. {@code "N.NN%"}</li>
 *   <li>{@code DECIMAL_PLACES}     — the number of implied decimal places taken
 *       from the last digit of the AI code, e.g. {@code "2"} for AI {@code 3942}.</li>
 * </ul>
 */
public final class DecimalPercentageEnricher implements InterpretationEnricherInterface {

    public DecimalPercentageEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (aiDefinition == null || baseValue == null) return Collections.emptyList();

        String ai = aiDefinition.getApplicationIdentifier();
        int decimalPlaces = Character.getNumericValue(ai.charAt(ai.length() - 1));

        String formatted = DecimalPointEnricher.applyDecimalPoint(baseValue, decimalPlaces);

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_PERCENTAGE, null, formatted + "%"));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.PERCENTAGE_FORMAT,  null,  buildFormat(decimalPlaces)));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_PLACES,     null,  String.valueOf(decimalPlaces)));
        return results;
    }

    // -------------------------------------------------------------------------

    /** Builds a display format string, e.g. decimalPlaces=2 → {@code "N.NN%"}. */
    private static String buildFormat(int decimalPlaces) {
        if (decimalPlaces == 0) return "N%";
        return "N." + "N".repeat(decimalPlaces) + "%";
    }
}
