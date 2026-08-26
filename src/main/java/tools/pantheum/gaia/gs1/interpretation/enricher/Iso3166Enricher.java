package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Enriches a 3-digit ISO 3166-1 numeric country code with the code itself
 * and its country name as two separate interpretations.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code COUNTRY_CODE_NUMERIC} — the ISO 3166-1 numeric code, e.g. {@code "036"}</li>
 *   <li>{@code COUNTRY_NAME} — the full country name, e.g. {@code "Australia"}</li>
 * </ul>
 * If the code is not found in the ISO 3166 dataset, an empty list is returned.
 */
public final class Iso3166Enricher implements InterpretationEnricherInterface {

    public Iso3166Enricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        Optional<String> name = Iso3166Data.nameForNumeric(baseValue);
        if (!name.isPresent()) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_CODE_NUMERIC, null, baseValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_NAME, null, name.get()));
        return results;
    }
}
