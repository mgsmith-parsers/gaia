package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.content.validator.ProductionMethodValidator;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 7010 (PROD METHOD — Production Method) by resolving the 1–2
 * character code to its human-readable description.
 *
 * <p>The code table is maintained in
 * {@link ProductionMethodValidator#ALLOWED_CODES}, aligned with GS1 General
 * Specifications AI 7010 and EU Regulation 1379/2013 (fisheries labelling).
 * Unrecognised codes produce a {@code PRODUCTION_METHOD_CODE} entry only;
 * no {@code PRODUCTION_METHOD} description is added.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code PRODUCTION_METHOD_CODE} — the raw code, e.g. {@code "2"}</li>
 *   <li>{@code PRODUCTION_METHOD}      — the description for recognised codes,
 *       e.g. {@code "Farmed (aquaculture, general)"}</li>
 * </ul>
 */
public final class ProductionMethodEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link ProductionMethodEnricher}. */
    public ProductionMethodEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.PRODUCTION_METHOD_CODE, null, baseValue));

        String description = ProductionMethodValidator.ALLOWED_CODES.get(baseValue);
        if (description != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.PRODUCTION_METHOD, null, description));
        }

        return results;
    }
}
