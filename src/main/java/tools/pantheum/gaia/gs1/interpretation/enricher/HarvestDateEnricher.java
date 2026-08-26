package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants_DateTime;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 7007 — HARVEST DATE ({@code YYMMDD[YYMMDD]}) with labelled
 * start-date, optional end-date, and combined date-range interpretations.
 *
 * <p>Reads the element's two date components directly rather than relying on
 * the generic {@link DateEnricher}, so that each date can be labelled
 * distinctly instead of producing duplicate {@code DATE_VALUE} entries.
 *
 * <h2>Component structure</h2>
 * <pre>
 *   Component 0 — Start date YYMMDD  (mandatory)
 *   Component 1 — End date   YYMMDD  (optional)
 * </pre>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code HARVEST_START_DATE} — formatted start date,
 *       e.g. {@code "15/06/2025"}</li>
 *   <li>{@code HARVEST_END_DATE}   — formatted end date (only when component 1
 *       is present and non-empty), e.g. {@code "20/06/2025"}</li>
 *   <li>{@code HARVEST_DATE_RANGE} — combined range string (only when an end
 *       date is present), e.g. {@code "15/06/2025 – 20/06/2025"}</li>
 *   <li>{@code DATE_FORMAT}        — the display format derived from the
 *       {@link ParseConfig}, e.g. {@code "dd/mm/yyyy"}</li>
 * </ul>
 * Returns an empty list if component 0 is absent or malformed.
 */
public final class HarvestDateEnricher implements InterpretationEnricherInterface {

    public HarvestDateEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                            GS1AIObjectElement element) {
        return enrich(baseValue, aiDefinition, element, ParseConfig.defaultConfig());
    }

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                            GS1AIObjectElement element, ParseConfig config) {
        if (element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return Collections.emptyList();

        // Component 0 — start date (mandatory)
        String startRaw = components.get(0).getValue();
        if (startRaw == null || startRaw.length() != 6) return Collections.emptyList();

        String startFormatted = DateEnricher.format(startRaw, GS1Constants_DateTime.FORMAT_YYMMDD, config);
        if (startFormatted == null) return Collections.emptyList();

        String datePat = DateEnricher.datePattern(config);

        List<GS1AIInterpretation> results = new ArrayList<>(4);
        results.add(new GS1AIInterpretation(
                GS1Constants_Enricher.HARVEST_START_DATE,
                null,
                startFormatted));

        // Component 1 — end date (optional)
        if (components.size() > 1) {
            String endRaw = components.get(1).getValue();
            if (endRaw != null && endRaw.length() == 6) {
                String endFormatted = DateEnricher.format(endRaw, GS1Constants_DateTime.FORMAT_YYMMDD, config);
                if (endFormatted != null) {
                    results.add(new GS1AIInterpretation(
                            GS1Constants_Enricher.HARVEST_END_DATE,
                            null,
                            endFormatted));
                    results.add(new GS1AIInterpretation(
                            GS1Constants_Enricher.HARVEST_DATE_RANGE,
                            null,
                            startFormatted + " – " + endFormatted));
                }
            }
        }

        results.add(new GS1AIInterpretation(
                GS1Constants_Enricher.DATE_FORMAT,
                null,
                datePat));

        return results;
    }
}
