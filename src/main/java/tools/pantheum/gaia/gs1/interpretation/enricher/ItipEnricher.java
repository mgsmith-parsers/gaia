package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches the GTIN-14 component of AI 8006 (ITIP) and AI 8026 (ITIP CONTENT)
 * with its GS1 company prefix and the corresponding GS1 member organisation.
 *
 * <p>Component 0 of an ITIP is a full GTIN-14:
 * <pre>
 *   [ Indicator (1 digit) ][ GS1 Company Prefix + item reference ][ Check digit ]
 * </pre>
 * The GS1 company prefix begins at the <em>second</em> digit, so the leading
 * indicator digit must be stripped before the prefix registry is consulted —
 * matching on the raw component would resolve the indicator digit as part of
 * the prefix and return the wrong member organisation.
 *
 * <p>{@link GS1PrefixEnricher} handles this for AI 01/02/03 via the
 * {@code "start": 1} extraction offset, but offsets are ignored when a
 * {@code component} is specified (see
 * {@code InterpretationEngine.extractBaseValue}), so ITIP needs its own
 * enricher. Wire it on component 0 in {@code ai-content.json}.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code GS1_MEMBER_CODE} — the matched prefix code from the GS1 prefix
 *       registry, e.g. {@code "950"}</li>
 *   <li>{@code GS1_MEMBER_NAME} — the member organisation name, e.g.
 *       {@code "GS1 Global Office"}</li>
 * </ul>
 * Returns an empty list if the value is too short to carry a prefix after the
 * indicator digit, or if no prefix is matched.
 */
public final class ItipEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link ItipEnricher}. */
    public ItipEnricher() {}

    /** Minimum characters required: the indicator digit plus at least one prefix digit. */
    private static final int MIN_LENGTH = 2;

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() < MIN_LENGTH) return Collections.emptyList();

        String withoutIndicator = baseValue.substring(1);

        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(withoutIndicator);
        if (match == null) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GS1_MEMBER_CODE, null, match[0]));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GS1_MEMBER_NAME, null, match[1]));
        return results;
    }
}
