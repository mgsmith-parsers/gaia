package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a GS1 key value with its matched GS1 company prefix and the
 * corresponding GS1 member organisation (MO) description.
 *
 * <p>The base value passed to {@link #enrich(String)} should begin at the
 * first digit of the GS1 company prefix (i.e. any leading indicator or
 * extension digit must already have been stripped by the extraction offset
 * in the {@link tools.pantheum.gaia.gs1.interpretation.InterpretationDefinition}).
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code GS1_COMPANY_PREFIX} — the full base value passed to the enricher
 *       (the slice defined by the interpretation definition), e.g. {@code "950600"}</li>
 *   <li>{@code GS1_MEMBER_CODE} — the matched prefix code from the GS1 prefix registry,
 *       e.g. {@code "950"}</li>
 *   <li>{@code GS1_MEMBER_NAME} — the member organisation name, e.g.
 *       {@code "GS1 Global Office"}</li>
 * </ul>
 * If no prefix is matched, an empty list is returned.
 */
public final class GS1PrefixEnricher implements InterpretationEnricherInterface {

    public GS1PrefixEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(baseValue);
        if (match == null) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        //results.add(new GS1AIInterpretation(GS1Constants_Enricher.GS1_COMPANY_PREFIX,  null, baseValue));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GS1_MEMBER_CODE,     null,    match[0]));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GS1_MEMBER_NAME,     null,    match[1]));
        return results;
    }
}
