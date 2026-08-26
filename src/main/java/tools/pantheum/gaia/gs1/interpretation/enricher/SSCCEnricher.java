package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.GS1PrefixRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 00 (SSCC — Serial Shipping Container Code) by decomposing the
 * 18-digit value into its constituent fields.
 *
 * <p>The SSCC is structured as:
 * <pre>
 *   [ Extension digit (1) ][ GS1 Company Prefix (7–12) ][ Serial Reference ][ Check digit (1) ]
 *   |&lt;--- index 0 -------&gt;||&lt;--- starts at index 1 -------------------------&gt;||&lt;-- index 17 --&gt;|
 * </pre>
 * The {@link GS1PrefixEnricher} handles the GS1 company prefix and member
 * organisation lookup. This enricher adds the surrounding fields.
 *
 * <p>This enricher must run <em>after</em> {@link GS1PrefixEnricher} in
 * {@code ai-content.json} so the prefix has already been identified
 * (both enrichers independently resolve the prefix boundary; this class
 * does its own lookup to compute the serial reference bounds).
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code SSCC_EXTENSION_DIGIT}  — single digit that precedes the GS1
 *       company prefix, allocated by the brand owner, e.g. {@code "3"}</li>
 *   <li>{@code SSCC_SERIAL_REFERENCE} — the serial reference portion
 *       (between the GS1 company prefix and the check digit),
 *       e.g. {@code "000134352"}</li>
 *   <li>{@code SSCC_CHECK_DIGIT}      — the modulo-10 check digit (last
 *       digit of the SSCC), e.g. {@code "0"}</li>
 * </ul>
 * Returns an empty list if the value is null, not exactly 18 digits, or the
 * GS1 company prefix cannot be matched.
 */
public final class SSCCEnricher implements InterpretationEnricherInterface {

    public SSCCEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != GS1Constants.SSCC_LENGTH) return Collections.emptyList();

        String extensionDigit = String.valueOf(baseValue.charAt(0));
        String checkDigit     = String.valueOf(baseValue.charAt(GS1Constants.SSCC_LENGTH - 1));

        // The GS1 company prefix begins immediately after the extension digit (index 1).
        String afterExtension = baseValue.substring(1);                      // 17 chars
        String[] match = GS1PrefixRegistry.INSTANCE.prefixMatchFor(afterExtension);
        if (match == null) return Collections.emptyList();

        // Serial reference: everything between the end of the prefix and the check digit
        //int prefixLength     = match[0].length();
        //String serialRef     = afterExtension.substring(prefixLength, afterExtension.length() - 1);

        List<GS1AIInterpretation> results = new ArrayList<>(3);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SSCC_EXTENSION_DIGIT,  null,  extensionDigit));
        //results.add(new GS1AIInterpretation(GS1Constants_Enricher.SSCC_SERIAL_REFERENCE, null, serialRef));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SSCC_CHECK_DIGIT,      null,      checkDigit));
        return results;
    }
}
