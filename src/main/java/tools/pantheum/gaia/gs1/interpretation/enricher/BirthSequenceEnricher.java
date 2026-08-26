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
 * Enriches AI 7258 (BIRTH SEQUENCE — Baby birth sequence) by splitting the
 * {@code posinseqslash} value into its constituent parts.
 *
 * <h2>Value format</h2>
 * The 3-character fixed-length value is structured as:
 * <pre>
 *   Char 0   : position in birth sequence (single digit, 1-based)
 *   Char 1   : literal {@code "/"}
 *   Char 2   : total number of babies born in this sequence
 * </pre>
 * Examples:
 * <ul>
 *   <li>{@code "1/1"} — single birth (one baby)</li>
 *   <li>{@code "1/2"} — first of twins</li>
 *   <li>{@code "2/3"} — second of triplets</li>
 * </ul>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code BIRTH_POSITION} — position in the sequence, e.g. {@code "2"}</li>
 *   <li>{@code BIRTH_TOTAL}    — total babies in this birth, e.g. {@code "3"}</li>
 *   <li>{@code BIRTH_SEQUENCE} — human-readable label, e.g. {@code "Baby 2 of 3"}</li>
 * </ul>
 * Returns an empty list if the value is null, not 3 characters, or not in
 * {@code digit/digit} format.
 */
public final class BirthSequenceEnricher implements InterpretationEnricherInterface {

    public BirthSequenceEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 3) return Collections.emptyList();

        char posChar   = baseValue.charAt(0);
        char slashChar = baseValue.charAt(1);
        char totChar   = baseValue.charAt(2);

        if (slashChar != '/' || !Character.isDigit(posChar) || !Character.isDigit(totChar)) {
            return Collections.emptyList();
        }

        String position = String.valueOf(posChar);
        String total    = String.valueOf(totChar);
        String sequence = "Baby " + position + " of " + total;

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.BIRTH_POSITION, null, position));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.BIRTH_TOTAL,    null,    total));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.BIRTH_SEQUENCE, null, sequence));
        return results;
    }
}
