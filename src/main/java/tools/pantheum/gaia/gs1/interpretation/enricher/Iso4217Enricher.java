package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.CurrencyEntry;
import tools.pantheum.gaia.gs1.dataset.Iso4217Data;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Enriches a 3-digit ISO 4217 numeric currency code with the numeric code
 * itself, the alpha code and the currency name as three separate interpretations.
 *
 * <p>Used by monetary AIs in the 391n and 393n ranges where component 0
 * carries the ISO 4217 numeric currency code ({@code N3, iso4217}).
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code CURRENCY_CODE}  — ISO 4217 numeric code, e.g. {@code "036"}</li>
 *   <li>{@code CURRENCY_ALPHA} — ISO 4217 alpha code, e.g. {@code "AUD"}</li>
 *   <li>{@code CURRENCY_NAME}  — currency name, e.g. {@code "Australian Dollar"}</li>
 * </ul>
 * If the code is not found in the ISO 4217 dataset, an empty list is returned.
 */
public final class Iso4217Enricher implements InterpretationEnricherInterface {

    public Iso4217Enricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null) return Collections.emptyList();

        Optional<CurrencyEntry> entryOpt = Iso4217Data.forNumeric(baseValue);
        if (!entryOpt.isPresent()) return Collections.emptyList();
        CurrencyEntry entry = entryOpt.get();

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CURRENCY_CODE,  null,  baseValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CURRENCY_ALPHA, null, entry.getCode()));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CURRENCY_NAME,  null,  entry.getName()));
        return results;
    }
}
