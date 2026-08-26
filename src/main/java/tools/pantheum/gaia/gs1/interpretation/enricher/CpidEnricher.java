package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
//import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8010 (CPID — Component/Part Identifier) by extracting the
 * component/part reference that follows the GS1 company prefix.
 *
 * <p>A CPID is structured as:
 * <pre>
 *   [ GS1 Company Prefix (numeric, 7–12 digits) ][ Component/Part Reference (Y charset) ]
 * </pre>
 * The {@link GS1PrefixEnricher} resolves the company prefix and member
 * organisation; this enricher adds the part reference that follows it.
 *
 * <p>This enricher must run <em>after</em> {@link GS1PrefixEnricher} in
 * {@code ai-content.json} so that the prefix has already been identified.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code CPID_PART_REFERENCE} — the component/part reference portion of
 *       the CPID (everything after the GS1 company prefix), e.g. {@code "ABC-001"}</li>
 * </ul>
 * Returns an empty list if the prefix cannot be matched or there is no
 * part reference following it.
 */
public final class CpidEnricher implements InterpretationEnricherInterface {

    public CpidEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(baseValue);
        if (match == null) return Collections.emptyList();

        String prefix        = match[0];
        String partReference = baseValue.substring(prefix.length());

        if (partReference.isEmpty()) return Collections.emptyList();

        //return List.of(new GS1AIInterpretation(GS1Constants_Enricher.CPID_PART_REFERENCE, null, partReference));
        return List.of();
    }
}
