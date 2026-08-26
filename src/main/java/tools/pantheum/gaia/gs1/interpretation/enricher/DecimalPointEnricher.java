package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Enriches a 6-digit numeric measurement value with a human-readable decimal
 * representation, using the last digit of the AI code as the number of
 * implied decimal places.
 *
 * <p>Used by AIs in the following ranges:
 * <ul>
 *   <li>31nn — weights and measures (metric)</li>
 *   <li>32nn — lengths and dimensions (imperial/US)</li>
 *   <li>35nn — areas and weights (imperial/US)</li>
 *   <li>36nn — volumes (imperial/US)</li>
 * </ul>
 *
 * <p>The last digit of the 4-digit AI code defines the number of implied
 * decimal places (0–5). For example, AI {@code 3102} has 2 decimal places,
 * so a raw value of {@code "001234"} is interpreted as {@code "12.34"}.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code DECIMAL_VALUE} — the value formatted with the implied decimal
 *       point, e.g. {@code "12.34"} for raw {@code "001234"} at 2 decimal
 *       places, or {@code "1234"} at 0 decimal places.</li>
 *   <li>{@code DECIMAL_PLACES} — the number of implied decimal places taken
 *       from the last digit of the AI code, e.g. {@code "2"} for AI {@code 3102}.</li>
 * </ul>
 */
public final class DecimalPointEnricher implements InterpretationEnricherInterface {

    public DecimalPointEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (aiDefinition == null) return Collections.emptyList();

        String ai = aiDefinition.getApplicationIdentifier();
        int decimalPlaces = Character.getNumericValue(ai.charAt(ai.length() - 1));

        String formatted = applyDecimalPoint(baseValue, decimalPlaces);
        return List.of(
                new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_VALUE,  null, formatted),
                new GS1AIInterpretation(GS1Constants_Enricher.DECIMAL_PLACES, null, String.valueOf(decimalPlaces)));
    }

    // -------------------------------------------------------------------------

    /**
     * Inserts a decimal point {@code decimalPlaces} digits from the right of
     * {@code raw}, stripping non-significant leading zeros from the integer part.
     *
     * <p>Examples (raw = 6 digits):
     * <pre>
     *   "001234", decimalPlaces=0  →  "1234"
     *   "001234", decimalPlaces=2  →  "12.34"
     *   "000050", decimalPlaces=2  →  "0.50"
     *   "000001", decimalPlaces=5  →  "0.00001"
     *   "000000", decimalPlaces=0  →  "0"
     * </pre>
     */
    static String applyDecimalPoint(String raw, int decimalPlaces) {
        if (decimalPlaces == 0) {
            // Strip leading zeros; keep at least one digit
            String stripped = raw.replaceFirst("^0+(?!$)", "");
            return stripped.isEmpty() ? "0" : stripped;
        }

        int len = raw.length();
        String intPart  = len > decimalPlaces ? raw.substring(0, len - decimalPlaces) : "0";
        String fracPart = len > decimalPlaces
                ? raw.substring(len - decimalPlaces)
                : String.format("%0" + decimalPlaces + "d", Long.parseLong(raw));

        // Strip leading zeros from integer part; keep at least "0"
        intPart = intPart.replaceFirst("^0+(?!$)", "");
        if (intPart.isEmpty()) intPart = "0";

        return intPart + "." + fracPart;
    }
}
