package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.List;

/**
 * Enriches temperature AIs expressed in Celsius (4331 — MAX TEMP C,
 * 4333 — MIN TEMP C) with a human-readable temperature value and unit.
 *
 * <h2>Component structure</h2>
 * <ul>
 *   <li>Component 0 ({@code N6}) — temperature magnitude in hundredths of a
 *       degree Celsius, e.g. {@code "002500"} = 25.00°C</li>
 *   <li>Component 1 (optional {@code [-]}) — if the literal {@code "-"} is
 *       present, the temperature is negative (below zero)</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code TEMPERATURE}           — decimal value, e.g. {@code "25.0"}
 *       or {@code "-10.5"}</li>
 *   <li>{@code TEMPERATURE_UNIT}      — {@code "°C"}</li>
 *   <li>{@code TEMPERATURE_FORMATTED} — combined, e.g. {@code "25.0°C"}
 *       or {@code "-10.5°C"}</li>
 * </ul>
 */
public final class TemperatureCelsiusEnricher implements InterpretationEnricherInterface {

    public TemperatureCelsiusEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        return TemperatureEnricherHelper.enrich(element, GS1Constants_Enricher.TEMPERATURE_UNIT_SYMBOL_C, GS1Constants_Enricher.TEMPERATURE_UNIT_NAME_C);
    }
}
