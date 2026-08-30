package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.Iso5218Data;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Enriches AI 7252 (BIO SEX — Biological sex) by mapping the single-digit
 * ISO 5218 code to a human-readable label.
 *
 * <h2>ISO 5218 codes</h2>
 * <table border="1">
 *   <caption>ISO 5218 biological sex codes</caption>
 *   <tr><th>Code</th><th>Meaning</th></tr>
 *   <tr><td>0</td><td>Not known</td></tr>
 *   <tr><td>1</td><td>Male</td></tr>
 *   <tr><td>2</td><td>Female</td></tr>
 *   <tr><td>9</td><td>Not applicable</td></tr>
 * </table>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code SEX_CODE}        — the ISO 5218 numeric code, e.g. {@code "1"}</li>
 *   <li>{@code SEX_DESCRIPTION} — the human-readable label, e.g. {@code "Male"}</li>
 * </ul>
 * Returns an empty list if the value is null, empty, or not a recognised code.
 */
public final class Iso5218Enricher implements InterpretationEnricherInterface {

    /** Creates a new {@link Iso5218Enricher}. */
    public Iso5218Enricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        Optional<String> label = Iso5218Data.forCode(baseValue);
        if (!label.isPresent()) return Collections.emptyList();

        return List.of(
                new GS1AIInterpretation(GS1Constants_Enricher.SEX_CODE,  null,  baseValue),
                new GS1AIInterpretation(GS1Constants_Enricher.SEX_DESCRIPTION, null, label.get())
        );
    }
}
