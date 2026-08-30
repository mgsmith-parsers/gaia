package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.dataset.CurrencyEntry;
import tools.pantheum.gaia.gs1.dataset.Iso4217Data;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Combines the {@code CURRENCY_ALPHA} and {@code DECIMAL_AMOUNT} interpretations
 * already present on the element into a {@code MONETARY_AMOUNT} and, when full
 * currency metadata is available, a {@code MONETARY_AMOUNT_DISPLAY} interpretation.
 *
 * <p>Designed to run <em>after</em> {@link Iso4217Enricher} and
 * {@link DecimalAmountEnricher} have been applied. It reads those values
 * from the element's interpretation list.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code MONETARY_AMOUNT} — stable machine-readable form, e.g. {@code "AUD 12.34"}
 *       or {@code "12.34"} when no currency is present. Always produced when
 *       {@code DECIMAL_AMOUNT} is present.</li>
 *   <li>{@code MONETARY_AMOUNT_DISPLAY} — symbol-formatted display form, e.g.
 *       {@code "A$12.34"} or {@code "1 234,56 kr"}. Only produced when a
 *       {@link CurrencyEntry} can be resolved from {@code CURRENCY_ALPHA} and
 *       the entry carries a non-blank symbol.</li>
 * </ul>
 *
 * <p>Display formatting rules applied from {@link CurrencyEntry}:
 * <ul>
 *   <li>{@code decimalPlaces}      — re-formats the numeric amount to the correct
 *       number of minor-unit digits.</li>
 *   <li>{@code thousandsSeparator} — inserts a thousands separator
 *       ({@code "comma"} → {@code ","}, {@code "period"} → {@code "."},
 *       {@code "space"} → {@code " "}, {@code "apostrophe"} → {@code "'"}).</li>
 *   <li>{@code symbol}             — prepended or appended according to
 *       {@code symbolPosition} ({@code "left"} or {@code "right"}).</li>
 * </ul>
 */
public final class MonetaryAmountEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link MonetaryAmountEnricher}. */
    public MonetaryAmountEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (element == null) return Collections.emptyList();

        String decimalAmount = findInterpretation(element, GS1Constants_Enricher.DECIMAL_AMOUNT);
        if (decimalAmount == null) return Collections.emptyList();

        String currencyAlpha  = findInterpretation(element, GS1Constants_Enricher.CURRENCY_ALPHA);
        String monetaryAmount = currencyAlpha != null
                ? currencyAlpha + " " + decimalAmount
                : decimalAmount;

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(
                GS1Constants_Enricher.MONETARY_AMOUNT, null, monetaryAmount));

        // Add the display interpretation only when we have currency metadata.
        if (currencyAlpha != null) {
            Iso4217Data.forAlpha(currencyAlpha).ifPresent(entry -> {
                String display = formatDisplay(decimalAmount, entry);
                if (display != null) {
                    results.add(new GS1AIInterpretation(
                            GS1Constants_Enricher.MONETARY_AMOUNT_DISPLAY,
                            null,
                            display));
                }
            });
        }

        return results;
    }

    // -------------------------------------------------------------------------

    /**
     * Formats the decimal amount string using the currency entry's display rules.
     * Returns {@code null} if the entry has no symbol or the amount cannot be parsed.
     */
    private static String formatDisplay(String decimalAmount, CurrencyEntry entry) {
        String symbol = entry.getSymbol();
        if (symbol == null || symbol.isBlank()) return null;

        // Parse the raw decimal amount (already has decimal point from DecimalAmountEnricher).
        double numericValue;
        try {
            numericValue = Double.parseDouble(decimalAmount);
        } catch (NumberFormatException e) {
            return null;
        }

        int    dp            = entry.getDecimalPlaces();
        String thousandsSep  = resolveThousandsSeparator(entry.getThousandsSeparator());
        String decimalSep    = resolveDecimalSeparator(entry.getDecimalCharacter());

        String formatted = formatNumber(numericValue, dp, thousandsSep, decimalSep);

        return "right".equalsIgnoreCase(entry.getSymbolPosition())
                ? formatted + " " + symbol
                : symbol + formatted;
    }

    /**
     * Formats a numeric value with the given decimal places, thousands separator,
     * and decimal separator.
     */
    private static String formatNumber(double value, int decimalPlaces,
                                       String thousandsSep, String decimalSep) {
        // Round to the required decimal places.
        long factor    = 1;
        for (int i = 0; i < decimalPlaces; i++) factor *= 10;
        long rounded   = Math.round(value * factor);
        long intPart   = rounded / factor;
        long fracPart  = Math.abs(rounded % factor);

        String intStr  = applyThousandsSeparator(Long.toString(intPart), thousandsSep);

        if (decimalPlaces == 0) return intStr;

        String fracStr = String.format("%0" + decimalPlaces + "d", fracPart);
        return intStr + decimalSep + fracStr;
    }

    /** Inserts a thousands separator into an integer string. */
    private static String applyThousandsSeparator(String intStr, String sep) {
        if (sep.isEmpty()) return intStr;
        StringBuilder sb  = new StringBuilder();
        int           len = intStr.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) sb.append(sep);
            sb.append(intStr.charAt(i));
        }
        return sb.toString();
    }

    /** Maps the {@link CurrencyEntry#getThousandsSeparator()} string to its character. */
    private static String resolveThousandsSeparator(String name) {
        if (name == null) return "";
        switch (name.toLowerCase()) {
            case "comma":       return ",";
            case "period":      return ".";
            case "space":       return " "; // narrow no-break space
            case "apostrophe":  return "'";
            default:            return "";
        }
    }

    /** Maps the {@link CurrencyEntry#getDecimalCharacter()} string to its character. */
    private static String resolveDecimalSeparator(String name) {
        if (name == null) return ".";
        switch (name.toLowerCase()) {
            case "comma":   return ",";
            case "period":  return ".";
            default:        return ".";
        }
    }

    private static String findInterpretation(GS1AIObjectElement element, String type) {
        return element.getInterpretations().stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst()
                .orElse(null);
    }
}
