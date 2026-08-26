package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants_DateTime;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.util.DateUtils;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8008 (PROD TIME — Date and time of production) with a complete
 * set of date, time and date-time interpretations.
 *
 * <p>AI 8008 uses two components with formats not handled by the generic
 * {@link DateEnricher} or {@link TimeEnricher}:
 * <ul>
 *   <li>Component 0 ({@code N8, yymmddhh}) — mandatory: date and hour
 *       encoded as {@code YYMMDDHH}, e.g. {@code "25061514"} =
 *       15 Jun 2025, 14:xx</li>
 *   <li>Component 1 ({@code N..4, mmoptss}) — optional: minutes with
 *       optional seconds encoded as {@code MM} or {@code MMSS},
 *       e.g. {@code "30"} = :30, {@code "3045"} = :30:45</li>
 * </ul>
 *
 * <p>This enricher is declared with no {@code component} or {@code start}/{@code end}
 * in {@code ai-content.json} and reads the component values directly from the
 * element rather than from the {@code baseValue} parameter.
 *
 * <h2>Produced interpretations</h2>
 * <p>Date formatting (order and separator) is controlled by the
 * {@link tools.pantheum.gaia.config.ParseConfig} passed to the pipeline.
 * The examples below use the default configuration (little-endian, slash).
 * <ul>
 *   <li>{@code DATE_VALUE}      — formatted date string, e.g. {@code "15/06/2025"}</li>
 *   <li>{@code DATE_FORMAT}     — date pattern string, e.g. {@code "dd/mm/yyyy"}</li>
 *   <li>{@code TIME_VALUE}      — {@code "HH:mm"} or {@code "HH:mm:ss"},
 *       e.g. {@code "14:30"} or {@code "14:30:45"}</li>
 *   <li>{@code TIME_FORMAT}     — {@code "HH:mm"} or {@code "HH:mm:ss"}</li>
 *   <li>{@code DATETIME_VALUE}  — combined date and time, e.g. {@code "15/06/2025 14:30:45"}</li>
 *   <li>{@code DATETIME_FORMAT} — combined pattern, e.g. {@code "dd/mm/yyyy HH:mm"} or
 *       {@code "dd/mm/yyyy HH:mm:ss"}</li>
 * </ul>
 * Returns an empty list if component 0 is missing or malformed.
 */
public final class ProductionTimeEnricher implements InterpretationEnricherInterface {

    public ProductionTimeEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        return enrich(baseValue, aiDefinition, element, ParseConfig.defaultConfig());
    }

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                            GS1AIObjectElement element, ParseConfig config) {
        if (element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return Collections.emptyList();

        // Component 0: YYMMDDHH (mandatory, 8 digits)
        String comp0 = components.get(0).getValue();
        if (comp0 == null || comp0.length() != 8) return Collections.emptyList();

        String yy   = comp0.substring(0, 2);
        String mm   = comp0.substring(2, 4);
        String dd   = comp0.substring(4, 6);
        String hh   = comp0.substring(6, 8);
        int resolvedYear = DateUtils.resolveYear(Integer.parseInt(yy));
        String yyyy = String.format("%04d", resolvedYear);

        String sep       = config.getDateSeparator().symbol();
        String dateValue = DateEnricher.assemble(config.getDateEndian(), sep, dd, mm, yyyy);
        String datePat   = DateEnricher.datePattern(config);

        // Component 1: MM[SS] (optional, 2 or 4 digits)
        String min = "00";
        String sec = null;

        if (components.size() > 1) {
            String comp1 = components.get(1).getValue();
            if (comp1 != null && comp1.length() >= 2) {
                min = comp1.substring(0, 2);
                if (comp1.length() == 4) {
                    sec = comp1.substring(2, 4);
                }
            }
        }

        String timeValue      = hh + ":" + min + (sec != null ? ":" + sec : "");
        String timeFormat     = GS1Constants_DateTime.DEFAULT_TIME_FORMAT + (sec != null ? ":ss" : "");
        String datetimeValue  = dateValue + " " + timeValue;
        String datetimeFormat = datePat + " " + timeFormat;

        List<GS1AIInterpretation> results = new ArrayList<>(6);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATE_VALUE,      null,      dateValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATE_FORMAT,     null,     datePat));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.TIME_VALUE,      null,      timeValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.TIME_FORMAT,     null,     timeFormat));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATETIME_VALUE,  null,  datetimeValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATETIME_FORMAT, null, datetimeFormat));
        return results;
    }
}
