package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Enriches a numeric amount value with a human-readable decimal representation,
 * using the last digit of the AI code as the number of implied decimal places.
 *
 * <p>Used by monetary AIs in the 390n, 391n, 392n and 393n ranges where the
 * amount component is variable-length ({@code N..15}).
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code DECIMAL_AMOUNT} — the value formatted with the implied decimal
 *       point, e.g. {@code "12.34"} for raw {@code "1234"} at 2 decimal
 *       places, or {@code "1234"} at 0 decimal places.</li>
 *   <li>{@code DECIMAL_PLACES} — the number of implied decimal places taken
 *       from the last digit of the AI code, e.g. {@code "2"} for AI {@code 3922}.</li>
 * </ul>
 */
public final class DecimalAmountEnricher implements InterpretationEnricherInterface {

    public DecimalAmountEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (aiDefinition == null || baseValue == null) return Collections.emptyList();

        String ai = aiDefinition.getApplicationIdentifier();
        int decimalPlaces = Character.getNumericValue(ai.charAt(ai.length() - 1));

        String formatted = DecimalPointEnricher.applyDecimalPoint(baseValue, decimalPlaces);
        return List.of(
                new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_AMOUNT, null, formatted),
                new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_PLACES, null, String.valueOf(decimalPlaces)));
    }
}
