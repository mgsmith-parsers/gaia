package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_DateTime;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a GS1 {@code HHmm} time component value with a human-readable
 * {@code HH:mm} representation and the display format.
 *
 * <p>The raw value is exactly 4 digits in the format {@code HHMM} where:
 * <ul>
 *   <li>{@code HH} — hours (00–23)</li>
 *   <li>{@code mm} — minutes (00–59)</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code TIME_VALUE}  — formatted time string {@code "HH:mm"},
 *       e.g. {@code "09:30"}</li>
 *   <li>{@code TIME_FORMAT} — the display format: {@code "HH:mm"}</li>
 * </ul>
 */
public final class TimeEnricher implements InterpretationEnricherInterface {

    public TimeEnricher() {}

    private static final String DISPLAY_FORMAT = GS1Constants_DateTime.DEFAULT_TIME_FORMAT;

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 4) return Collections.emptyList();

        String formatted = format(baseValue);
        if (formatted == null) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.TIME_VALUE,  null,  formatted));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.TIME_FORMAT, null, DISPLAY_FORMAT));
        return results;
    }

    // -------------------------------------------------------------------------

    /**
     * Formats a raw 4-digit {@code HHMM} string as {@code "HH:mm"}.
     * Returns {@code null} if the value cannot be parsed or is out of range.
     */
    static String format(String raw) {
        try {
            int hh = Integer.parseInt(raw.substring(0, 2));
            int mm = Integer.parseInt(raw.substring(2, 4));
            if (hh > 23 || mm > 59) return null;
            return String.format("%02d:%02d", hh, mm);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
