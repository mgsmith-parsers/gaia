package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AIs 7230–7239 (CERT # 0 – CERT # 9 — Certification Reference) by
 * splitting the value into its constituent parts and resolving the certification
 * scheme code to a human-readable name.
 *
 * <p>The fourth digit of the AI code is the sequence number (0–9), allowing up
 * to ten concurrent certification references on a single trade item or asset.
 * The element string must be processed together with the GTIN of the trade item
 * or the GIAI of the asset to which it relates.
 *
 * <h2>Component structure</h2>
 * <ul>
 *   <li>Component 0 ({@code X2}, fixed) — certification scheme code (2 characters),
 *       as defined by GS1. Currently defined codes:
 *       <ul>
 *         <li>{@code "EM"} — European Marine Equipment Directive
 *             (EU Regulation 2018/608)</li>
 *       </ul>
 *   </li>
 *   <li>Component 1 ({@code X..28}, variable) — certification reference number
 *       (up to 28 characters)</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code CERT_SEQUENCE}     — sequence slot derived from the last digit of
 *       the AI code, e.g. {@code "3"} for AI 7233</li>
 *   <li>{@code CERT_SCHEME_CODE}  — 2-character certification scheme code,
 *       e.g. {@code "EM"}</li>
 *   <li>{@code CERT_SCHEME_NAME}  — human-readable scheme name if the code is
 *       recognised, e.g. {@code "European Marine Equipment Directive"};
 *       omitted for unknown codes</li>
 *   <li>{@code CERT_REFERENCE}    — certification reference number</li>
 * </ul>
 * Returns an empty list if the AI definition is absent or either component
 * is missing.
 */
public final class CertificationReferenceEnricher implements InterpretationEnricherInterface {

    public CertificationReferenceEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (aiDefinition == null || element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 2) return Collections.emptyList();

        String schemeCode = components.get(0).getValue();
        String reference  = components.get(1).getValue();

        if (schemeCode == null || schemeCode.isEmpty()) return Collections.emptyList();
        if (reference  == null || reference.isEmpty())  return Collections.emptyList();

        // Sequence number is the last digit of the AI code (e.g. "7233" → "3")
        String ai       = aiDefinition.getApplicationIdentifier();
        String sequence = String.valueOf(ai.charAt(ai.length() - 1));

        List<GS1AIInterpretation> results = new ArrayList<>(4);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CERT_SEQUENCE,    null,    sequence));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CERT_SCHEME_CODE, null, schemeCode));

        String schemeName = GS1Constants.CERT_SCHEME_NAMES.get(schemeCode.toUpperCase());
        if (schemeName != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.CERT_SCHEME_NAME, null, schemeName));
        }

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.CERT_REFERENCE, null, reference));
        return results;
    }
}
