package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.config.ParseConfig;
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
 * Combines the {@code DATE_VALUE} and {@code TIME_VALUE} interpretations
 * already present on the element into a single date-time interpretation.
 *
 * <p>This enricher is designed to run <em>after</em> {@link DateEnricher} and
 * {@link TimeEnricher} have been applied to their respective components. It
 * reads the previously produced {@code DATE_VALUE} and {@code TIME_VALUE}
 * from the element's interpretation list and merges them.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code DATETIME_VALUE}  — combined date and time string using the
 *       already-formatted {@code DATE_VALUE} and {@code TIME_VALUE},
 *       e.g. {@code "15/06/2025 14:30"} (little-endian, {@code "/"} separator)</li>
 *   <li>{@code DATETIME_FORMAT} — the display format derived from the
 *       {@link ParseConfig} and the {@code TIME_FORMAT} already on the element,
 *       e.g. {@code "dd/mm/yyyy HH:mm"} or {@code "yyyy-mm-dd HH:mm:ss"}</li>
 * </ul>
 * If either {@code DATE_VALUE} or {@code TIME_VALUE} is absent from the
 * element's interpretations, an empty list is returned.
 *
 * <h2>JSON definition</h2>
 * Add this enricher as the last interpretation entry (after the date and time
 * definitions) so that {@code DATE_VALUE} and {@code TIME_VALUE} are guaranteed
 * to be present when it runs:
 * <pre>
 * { "enricher": "DateTimeEnricher" }
 * </pre>
 */
public final class DateTimeEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link DateTimeEnricher}. */
    public DateTimeEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        return enrich(baseValue, aiDefinition, element, ParseConfig.defaultConfig());
    }

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                            GS1AIObjectElement element, ParseConfig config) {
        if (element == null) return Collections.emptyList();

        // DATE_VALUE is already formatted by DateEnricher using the same config.
        String dateValue = findInterpretation(element, GS1Constants_Enricher.DATE_VALUE);
        String timeValue = findInterpretation(element, GS1Constants_Enricher.TIME_VALUE);

        if (dateValue == null || timeValue == null) return Collections.emptyList();

        // TIME_FORMAT is produced by TimeEnricher (e.g. "HH:mm" or "HH:mm:ss").
        // Fall back to the default if not present.
        String timeFormat     = findInterpretation(element, GS1Constants_Enricher.TIME_FORMAT);
        if (timeFormat == null) timeFormat = GS1Constants_DateTime.DEFAULT_TIME_FORMAT;

        String combined       = dateValue + GS1Constants_DateTime.DATE_TIME_SEPARATOR + timeValue;
        String datetimeFormat = DateEnricher.datePattern(config) + GS1Constants_DateTime.DATE_TIME_SEPARATOR + timeFormat;

        List<GS1AIInterpretation> results = new ArrayList<>(GS1Constants_DateTime.INTERPRETATION_COUNT);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATETIME_VALUE,  null,  combined));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATETIME_FORMAT, null, datetimeFormat));
        return results;
    }

    // -------------------------------------------------------------------------

    /**
     * Finds the value of the first interpretation with the given type on the
     * element, or {@code null} if none is found.
     */
    private static String findInterpretation(GS1AIObjectElement element, String type) {
        return element.getInterpretations().stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst()
                .orElse(null);
    }
}
