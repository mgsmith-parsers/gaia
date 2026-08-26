package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a GS1 {@code pieceoftotal} field by splitting the 4-digit
 * {@code PPTT} value into its piece number and total count.
 *
 * <p>Used in:
 * <ul>
 *   <li>AI 8006 (ITIP) — component 1</li>
 *   <li>AI 8026 (ITIP CONTENT) — component 1</li>
 * </ul>
 *
 * <h2>Value format</h2>
 * <pre>
 *   Digits 0–1 : PP — piece number (01–99)
 *   Digits 2–3 : TT — total number of pieces (01–99)
 * </pre>
 * e.g. {@code "0205"} = piece 2 of 5.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code PIECE_NUMBER}   — piece number with leading zeros stripped,
 *       e.g. {@code "2"}</li>
 *   <li>{@code PIECE_TOTAL}    — total pieces with leading zeros stripped,
 *       e.g. {@code "5"}</li>
 *   <li>{@code PIECE_OF_TOTAL} — combined label, e.g. {@code "2 of 5"}</li>
 * </ul>
 * Returns an empty list if the value is null or not exactly 4 digits.
 */
public final class PieceOfTotalEnricher implements InterpretationEnricherInterface {

    public PieceOfTotalEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 4) return Collections.emptyList();

        String pieceStr = baseValue.substring(0, 2);
        String totalStr = baseValue.substring(2, 4);

        // Strip leading zeros (01 → "1", 10 → "10")
        String piece = String.valueOf(Integer.parseInt(pieceStr));
        String total = String.valueOf(Integer.parseInt(totalStr));

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.PIECE_NUMBER,   null,   piece));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.PIECE_TOTAL,    null,    total));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.PIECE_OF_TOTAL, null, piece + " of " + total));
        return results;
    }
}
