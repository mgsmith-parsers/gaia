package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 7241 (AIDC MEDIA TYPE) by resolving the 2-digit code to its
 * human-readable AIDC media type description.
 *
 * <p>The code table is maintained in {@link GS1Constants#AIDC_ASSIGNED_CODES}
 * (codes {@code 01}–{@code 10}, as defined by ICCBBA). Unrecognised or reserved
 * codes produce a {@code MEDIA_TYPE_CODE} entry only — no {@code MEDIA_TYPE_NAME}.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code MEDIA_TYPE_CODE} — the raw 2-digit code, e.g. {@code "03"}</li>
 *   <li>{@code MEDIA_TYPE_NAME} — the description for assigned codes,
 *       e.g. {@code "Sample tube"} (omitted for reserved/unassigned codes)</li>
 * </ul>
 */
public final class AIDCMediaTypeEnricher implements InterpretationEnricherInterface {

    public AIDCMediaTypeEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.MEDIA_TYPE_CODE, null, baseValue));

        String name = GS1Constants.AIDC_ASSIGNED_CODES.get(baseValue);
        if (name != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.MEDIA_TYPE_NAME, null, name));
        }

        return results;
    }
}
