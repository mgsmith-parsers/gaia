package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.List;

/**
 * Enriches temperature AIs expressed in Fahrenheit (4330 — MAX TEMP F,
 * 4332 — MIN TEMP F) with a human-readable temperature value and unit.
 *
 * <h2>Component structure</h2>
 * <ul>
 *   <li>Component 0 ({@code N6}) — temperature magnitude in hundredths of a
 *       degree Fahrenheit, e.g. {@code "003200"} = 32.00°F</li>
 *   <li>Component 1 (optional {@code [-]}) — if the literal {@code "-"} is
 *       present, the temperature is negative (below zero)</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code TEMPERATURE}           — decimal value, e.g. {@code "32.0"}
 *       or {@code "-4.0"}</li>
 *   <li>{@code TEMPERATURE_UNIT}      — {@code "°F"}</li>
 *   <li>{@code TEMPERATURE_FORMATTED} — combined, e.g. {@code "32.0°F"}
 *       or {@code "-4.0°F"}</li>
 * </ul>
 */
public final class TemperatureFahrenheitEnricher implements InterpretationEnricherInterface {

    public TemperatureFahrenheitEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        return TemperatureEnricherHelper.enrich(element, GS1Constants_Enricher.TEMPERATURE_UNIT_SYMBOL_F, GS1Constants_Enricher.TEMPERATURE_UNIT_NAME_F);
    }
}
