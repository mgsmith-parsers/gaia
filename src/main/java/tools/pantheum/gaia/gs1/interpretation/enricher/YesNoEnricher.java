package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Enriches GS1 {@code yesno} flag fields by mapping the single-digit value
 * to a human-readable Yes/No label.
 *
 * <p>Applied to boolean flag AIs where {@code 1} means true/yes and
 * {@code 0} means false/no:
 * <ul>
 *   <li>AI 4321 — Dangerous goods flag</li>
 *   <li>AI 4322 — Authority to leave</li>
 *   <li>AI 4323 — Signature required flag</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code FLAG_VALUE} — {@code "Yes"} for {@code "1"},
 *       {@code "No"} for {@code "0"}</li>
 * </ul>
 * Returns an empty list if the value is null, empty, or not {@code "0"} or {@code "1"}.
 */
public final class YesNoEnricher implements InterpretationEnricherInterface {

    public YesNoEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        String label = GS1Constants.FLAG_YES_VALUE.equals(baseValue) ? GS1Constants_Enricher.FLAG_YES_LABEL
                     : GS1Constants.FLAG_NO_VALUE.equals(baseValue)  ? GS1Constants_Enricher.FLAG_NO_LABEL
                     : null;

        if (label == null) return Collections.emptyList();
        return List.of(new GS1AIInterpretation(GS1Constants_Enricher.FLAG_VALUE, null, label));
    }
}
