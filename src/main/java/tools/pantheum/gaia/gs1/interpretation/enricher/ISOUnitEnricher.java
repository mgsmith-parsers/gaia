package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.constants.GS1Constants_UOM;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a measurement AI value with the corresponding ISO unit of measure
 * code and name, using the first three digits of the AI code to identify the
 * unit group.
 *
 * <p>Unit codes follow UN/ECE Recommendation 20 (also aligned with ISO 80000).
 * Used by AIs in the 31nn, 32nn, 33nn, 34nn, 35nn and 36nn ranges, all of
 * which share the pattern: 4-digit AI where the last digit encodes implied
 * decimal places, and the first three digits identify the physical quantity
 * and unit.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code ISO_UNIT_CODE} — UN/ECE Rec. 20 unit code, e.g. {@code "KGM"}</li>
 *   <li>{@code ISO_UNIT_NAME} — human-readable unit name, e.g. {@code "Kilogram"}</li>
 * </ul>
 * If the AI prefix is not in the unit map, an empty list is returned.
 */
public final class ISOUnitEnricher implements InterpretationEnricherInterface {

    public ISOUnitEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (aiDefinition == null) return Collections.emptyList();

        String ai = aiDefinition.getApplicationIdentifier();
        if (ai.length() < 3) return Collections.emptyList();

        String[] unit = GS1Constants_UOM.UNIT_MAP.get(ai.substring(0, 3));
        if (unit == null) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(2);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.ISO_UNIT_CODE, null, unit[0]));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.ISO_UNIT_NAME, null, unit[1]));
        return results;
    }
}
