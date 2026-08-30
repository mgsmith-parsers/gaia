package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.content.validator.GTINValidator;
import tools.pantheum.gaia.gs1.util.GS1Utils;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches GTIN-bearing AIs (01, 02) by identifying the native GTIN format
 * and, for GTIN-14, the packaging level indicated by the first digit.
 *
 * <h2>GTIN format detection</h2>
 * All GTINs are stored as 14 digits in the element string. The native format
 * is determined by counting leading zeros:
 * <pre>
 *   000000XXXXXXXX  →  GTIN-8   (6 leading zeros)
 *   00XXXXXXXXXXXX  →  GTIN-12  (2 leading zeros)
 *   0XXXXXXXXXXXXX  →  GTIN-13  (1 leading zero)
 *   1–9XXXXXXXXXXX  →  GTIN-14  (indicator digit 1–9)
 * </pre>
 *
 * <h2>Packaging level (GTIN-14 only)</h2>
 * For GTIN-14 values (indicator digit 1–9) the first digit encodes a
 * packaging level:
 * <ul>
 *   <li>1–8 — Logistic unit (level 1 through 8)</li>
 *   <li>9   — Variable measure trade item</li>
 * </ul>
 * Indicator digit {@code 0} indicates a padded GTIN-8/12/13 — no packaging
 * level is emitted in that case.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code GTIN_TYPE}        — native GTIN type, e.g. {@code "GTIN-13"}</li>
 *   <li>{@code GTIN_NATIVE}        — GTIN without leading-zero padding,
 *       e.g. {@code "9506000134352"} for a GTIN-13</li>
 *   <li>{@code PACKAGING_LEVEL}    — human-readable packaging level for GTIN-14,
 *       e.g. {@code "Logistic unit (level 2)"}; omitted for GTIN-8/12/13</li>
 *   <li>{@code GTIN_CHECK_DIGIT}   — the modulo-10 check digit (the final digit),
 *       e.g. {@code "2"}</li>
 * </ul>
 * Returns an empty list if the value is null or not 14 digits.
 */
public final class GTINEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link GTINEnricher}. */
    public GTINEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 14) return Collections.emptyList();

        String format = GS1Utils.detectFormat(baseValue);
        String native_ = GS1Utils.nativeValue(baseValue);

        List<GS1AIInterpretation> results = new ArrayList<>(4);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GTIN_TYPE, null, format));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GTIN_NATIVE, null, native_));

        // Packaging level applies only to GTIN-14 (non-zero indicator digit)
        String level = GTINValidator.packagingLevel(baseValue.charAt(0));
        if (level != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.PACKAGING_LEVEL, null, level));
        }

        // Check digit — the final digit of the 14-digit value
        String checkDigit = String.valueOf(baseValue.charAt(13));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.GTIN_CHECK_DIGIT, null, checkDigit));

        return results;
    }
}
