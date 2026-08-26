package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared temperature decoding logic for {@link TemperatureCelsiusEnricher}
 * and {@link TemperatureFahrenheitEnricher}.
 *
 * <p>GS1 temperature AIs (4330–4333) encode the magnitude as hundredths of a
 * degree in component 0 ({@code N6}), with an optional {@code "-"} sign
 * character in component 1 indicating a negative (below-zero) temperature.
 */
final class TemperatureEnricherHelper {

    private TemperatureEnricherHelper() {}

    /**
     * Decodes the temperature from the element's component values and returns
     * the three standard temperature interpretations.
     *
     * @param element    the parent element providing component values
     * @param unitSymbol the temperature unit symbol, e.g. {@code "°C"} or {@code "°F"}
     * @param unitName   the full unit description, e.g. {@code "Celsius (°C)"} or
     *                   {@code "Fahrenheit (°F)"}
     * @return list of interpretations, or empty if decoding fails
     */
    static List<GS1AIInterpretation> enrich(GS1AIObjectElement element, String unitSymbol, String unitName) {
        if (element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return Collections.emptyList();

        String raw = components.get(0).getValue();
        if (raw == null) return Collections.emptyList();

        // Component 1 (optional): "-" indicates negative temperature
        boolean negative = components.size() > 1
                && "-".equals(components.get(1).getValue());

        try {
            long hundredths = Long.parseLong(raw);
            double temp = hundredths / 100.0;
            if (negative) temp = -temp;

            String tempStr = formatTemperature(temp);

            List<GS1AIInterpretation> results = new ArrayList<>(3);
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.TEMPERATURE,           null,           tempStr));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.TEMPERATURE_UNIT,      null,      unitName));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.TEMPERATURE_FORMATTED, null, tempStr + " " + unitSymbol));
            return results;

        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Formats a temperature value stripping unnecessary trailing decimal zeros
     * but keeping at least one decimal place. e.g. {@code 25.0}, {@code -10.5},
     * {@code 100.25}.
     */
    private static String formatTemperature(double value) {
        String s = String.format("%.2f", value);
        s = s.replaceAll("0+$", "");
        if (s.endsWith(".")) s += "0";
        return s;
    }
}
