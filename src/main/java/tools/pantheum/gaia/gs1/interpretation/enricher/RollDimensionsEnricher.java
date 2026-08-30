package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.content.componentformat.WindingValidator;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8001 (DIMENSIONS — Roll products) by decomposing the 14-digit
 * value into its five labelled measurement fields.
 *
 * <h2>Value structure (14 digits)</h2>
 * <pre>
 *   Component 0 (N4) : Roll width       — millimetres,  e.g. {@code "1500"} = 1500 mm
 *   Component 1 (N5) : Roll length      — metres,       e.g. {@code "02000"} = 2000 m
 *   Component 2 (N3) : Core diameter    — millimetres,  e.g. {@code "076"} = 76 mm
 *   Component 3 (N1) : Winding direction — 0 = not defined,
 *                                          1 = clockwise (viewed from outside),
 *                                          2 = counterclockwise (viewed from outside)
 *   Component 4 (N1) : Number of splices — 0–9
 * </pre>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code ROLL_WIDTH}            — roll width in mm, leading zeros stripped,
 *       e.g. {@code "1500"}</li>
 *   <li>{@code ROLL_LENGTH}           — roll length in m, leading zeros stripped,
 *       e.g. {@code "2000"}</li>
 *   <li>{@code CORE_DIAMETER}         — core diameter in mm, leading zeros stripped,
 *       e.g. {@code "76"}</li>
 *   <li>{@code WINDING_DIRECTION_CODE}— single-digit winding code, e.g. {@code "1"}</li>
 *   <li>{@code WINDING_DIRECTION}     — human-readable direction,
 *       e.g. {@code "Clockwise"}; omitted if code is {@code "0"} (not defined)</li>
 *   <li>{@code SPLICES}               — number of splices, e.g. {@code "2"}</li>
 * </ul>
 * Returns an empty list if the element has fewer than 5 components.
 */
public final class RollDimensionsEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link RollDimensionsEnricher}. */
    public RollDimensionsEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 5) return Collections.emptyList();

        String width     = stripLeadingZeros(components.get(0).getValue());
        String length    = stripLeadingZeros(components.get(1).getValue());
        String coreDiam  = stripLeadingZeros(components.get(2).getValue());
        String windCode  = components.get(3).getValue();
        String splices   = components.get(4).getValue();

        if (width == null || length == null || coreDiam == null
                || windCode == null || splices == null) {
            return Collections.emptyList();
        }

        List<GS1AIInterpretation> results = new ArrayList<>(6);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.ROLL_WIDTH,    null,    width));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.ROLL_LENGTH,   null,   length));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CORE_DIAMETER, null, coreDiam));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.WINDING_DIRECTION_CODE, null, windCode));

        String windName = WindingValidator.nameForCode(windCode);
        if (windName != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.WINDING_DIRECTION, null, windName));
        }

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPLICES, null, splices));
        return results;
    }

    // -------------------------------------------------------------------------

    private static String stripLeadingZeros(String value) {
        if (value == null) return null;
        String stripped = value.replaceFirst("^0+", "");
        return stripped.isEmpty() ? "0" : stripped;
    }

}
