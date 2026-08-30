package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8013 (GMN — Global Model Number) by decomposing the value into
 * its model reference and check character pair.
 *
 * <p>The GMN is structured as:
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Model Reference (alphanumeric) ][ Check Pair (2 chars) ]
 * </pre>
 * The {@link GS1PrefixEnricher} handles the company prefix and member
 * organisation lookup. This enricher adds the remaining fields.
 *
 * <p>This enricher must run <em>after</em> {@link GS1PrefixEnricher} in
 * {@code ai-content.json}.
 *
 * <h2>Example</h2>
 * GMN {@code "1987654Ad4X4bL5ttr2310c2K"}:
 * <ul>
 *   <li>GS1 company prefix: {@code "1987654"}</li>
 *   <li>Model reference:     {@code "Ad4X4bL5ttr2310c"}</li>
 *   <li>Check pair:          {@code "2K"}</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code GMN_MODEL_REFERENCE} — the model reference portion of the GMN,
 *       e.g. {@code "Ad4X4bL5ttr2310c"}</li>
 *   <li>{@code GMN_CHECK_PAIR}      — the two-character MOD 1021,32 check pair,
 *       e.g. {@code "2K"}</li>
 * </ul>
 * Returns an empty list if the prefix cannot be matched or the value is too
 * short to contain a model reference after the prefix and check pair.
 */
public final class GMNEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link GMNEnricher}. */
    public GMNEnricher() {}

    /** Minimum characters required after the prefix: at least 1 model reference char + 2 check chars. */
    private static final int MIN_SUFFIX_LENGTH = 3;

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(baseValue);
        if (match == null) return Collections.emptyList();

        String prefix = match[0];
        String suffix = baseValue.substring(prefix.length()); // model reference + check pair

        if (suffix.length() < MIN_SUFFIX_LENGTH) return Collections.emptyList();

        String checkPair       = suffix.substring(suffix.length() - 2);
        //String modelReference  = suffix.substring(0, suffix.length() - 2);

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        //results.add(new GS1AIInterpretation(GS1Constants_Enricher.GMN_MODEL_REFERENCE, null, modelReference));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GMN_CHECK_PAIR,      null,      checkPair));
        return results;
    }
}
