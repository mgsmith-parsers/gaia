package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches GS1 {@code iso3166alpha2} fields containing a 2-character
 * ISO 3166-1 alpha-2 country code by resolving it to a human-readable name.
 *
 * <p>Used by:
 * <ul>
 *   <li>AI 4307 — Ship-to / Deliver-to country code</li>
 *   <li>AI 4317 — Return-to country code</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code COUNTRY_CODE_ALPHA2} — the ISO 3166-1 alpha-2 code, e.g. {@code "AU"}</li>
 *   <li>{@code COUNTRY_NAME} — the full country name, e.g. {@code "Australia"};
 *       omitted if the code is not found in the ISO 3166 dataset</li>
 * </ul>
 * Returns an empty list if the value is null or empty.
 */
public final class Iso3166Alpha2Enricher implements InterpretationEnricherInterface {

    /** Creates a new {@link Iso3166Alpha2Enricher}. */
    public Iso3166Alpha2Enricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_CODE_ALPHA2, null, baseValue));

        Iso3166Data.nameForAlpha2(baseValue.toUpperCase()).ifPresent(name ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_NAME, null, name)));

        return results;
    }
}
