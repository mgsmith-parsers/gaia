package tools.pantheum.gaia.gs1.interpretation.enricher;

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
 * Enriches AI 7040 (UIC+EXT — GS1 UIC with Extension 1 and Importer Index)
 * by decomposing the 4-character value into its constituent fields.
 *
 * <p>AI 7040 is used for the traceability of tobacco products under EU
 * Regulation 2018/574. It encodes the 2-character Unique Identification Code
 * (UIC) of the ID Issuer, the Extension 1 character that identifies the
 * National Authority that appointed the issuer, and an optional Importer Index.
 *
 * <h2>Component structure (total 4 characters)</h2>
 * <pre>
 *   [ UIC digit (N1) ][ UIC char (X1) ][ Extension 1 (X1) ][ Importer index (X1) ]
 *   Component 0        Component 1       Component 2          Component 3
 *   e.g.  "3"          "A"               "B"                  "C"  →  UIC = "3A"
 * </pre>
 *
 * <h2>Importer Index semantics</h2>
 * <ul>
 *   <li>{@code A–Z} — identifies one of up to 26 importers</li>
 *   <li>{@code a–z} — identifies one of up to 26 further importers</li>
 *   <li>{@code 0–9} — identifies one of up to 10 further importers</li>
 *   <li>{@code -}   — identifies one further importer</li>
 *   <li>{@code _}   — importer index does not apply (domestic product)</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code UIC_CODE}           — the 2-character UIC of the ID Issuer
 *       (components 0 + 1 concatenated), e.g. {@code "3A"}</li>
 *   <li>{@code UIC_EXTENSION_1}    — Extension 1 character identifying the
 *       appointing National Authority, e.g. {@code "B"}</li>
 *   <li>{@code UIC_IMPORTER_INDEX} — Importer Index character, or the string
 *       {@code "Not applicable"} when the value is {@code "_"}</li>
 * </ul>
 * Returns an empty list if fewer than 4 components are present.
 */
public final class UicEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link UicEnricher}. */
    public UicEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (element == null) return Collections.emptyList();

        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.size() < 4) return Collections.emptyList();

        String uicDigit      = components.get(0).getValue();
        String uicChar       = components.get(1).getValue();
        String extension1    = components.get(2).getValue();
        String importerIndex = components.get(3).getValue();

        if (uicDigit == null || uicChar == null || extension1 == null || importerIndex == null) {
            return Collections.emptyList();
        }

        String uicCode = uicDigit + uicChar;

        String importerDisplay = "_".equals(importerIndex) ? "Not applicable" : importerIndex;

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.UIC_CODE,           null,           uicCode));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.UIC_EXTENSION_1,    null,    extension1));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.UIC_IMPORTER_INDEX, null, importerDisplay));
        return results;
    }
}
