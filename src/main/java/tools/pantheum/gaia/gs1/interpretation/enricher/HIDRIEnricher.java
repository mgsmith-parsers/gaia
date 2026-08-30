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
 * Enriches AI 8014 (MUDI — Highly Individualised Device Registration
 * Identifier, HIDRI) by decomposing the value into its device reference
 * and check character pair.
 *
 * <p>The HIDRI is structured as:
 * <pre>
 *   [ GS1 Company Prefix (numeric) ][ Device Reference (alphanumeric) ][ Check Pair (2 chars) ]
 * </pre>
 * The {@link GS1PrefixEnricher} handles the company prefix and member
 * organisation lookup. This enricher adds the remaining fields.
 *
 * <p>This enricher must run <em>after</em> {@link GS1PrefixEnricher} in
 * {@code ai-content.json}.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code HIDRI_DEVICE_REFERENCE} — the device reference portion of the
 *       HIDRI (between the GS1 company prefix and the check pair)</li>
 *   <li>{@code HIDRI_CHECK_PAIR}       — the two-character MOD 1021,32 check
 *       pair (last two characters of the value)</li>
 * </ul>
 * Returns an empty list if the prefix cannot be matched or the remaining
 * value is too short to contain a device reference and check pair.
 */
public final class HIDRIEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link HIDRIEnricher}. */
    public HIDRIEnricher() {}

    /** Minimum characters required after the prefix: at least 1 device reference char + 2 check chars. */
    private static final int MIN_SUFFIX_LENGTH = 3;

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(baseValue);
        if (match == null) return Collections.emptyList();

        String prefix  = match[0];
        String suffix  = baseValue.substring(prefix.length()); // device reference + check pair

        if (suffix.length() < MIN_SUFFIX_LENGTH) return Collections.emptyList();

        String checkPair        = suffix.substring(suffix.length() - 2);
        //String deviceReference  = suffix.substring(0, suffix.length() - 2);

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        //results.add(new GS1AIInterpretation(GS1Constants_Enricher.HIDRI_DEVICE_REFERENCE, null, deviceReference));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.HIDRI_CHECK_PAIR,       null,       checkPair));
        return results;
    }
}
