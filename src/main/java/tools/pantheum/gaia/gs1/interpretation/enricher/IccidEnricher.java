package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8043 (PSIM) by surfacing the parts of the physical SIM's ICCID
 * (ITU-T E.118 integrated circuit card identifier).
 *
 * <p>AI 8043 is declared {@code N18+[N1..N2]} — an 18-digit body plus an optional
 * 1–2 digit extension, so 18 to 20 digits in total.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code SIM_MII} — the 2-digit Major Industry Identifier, {@code "89"} for
 *       telecommunications (ITU-T E.118; ISO/IEC 7812's MII is its leading digit).
 *       Unlike {@code EidEnricher} this does not emit {@code SIM_MII_NAME}.</li>
 *   <li>{@code ICCID_BODY} — the remaining digits of the 18-digit first component</li>
 *   <li>{@code ICCID_EXTENSION} — the optional second component, emitted only when
 *       the value actually carries it</li>
 * </ul>
 *
 * <h2>What this deliberately does not do</h2>
 * Unlike {@code EidEnricher}, no check digit is surfaced and no country-code /
 * issuer-identifier split is attempted. E.118 defines a trailing Luhn digit, but with
 * a variable 18–20 digit length its presence and position cannot be determined from
 * the value alone, and the issuer-identifier boundary is issuer-dependent. Presenting
 * either would be a guess dressed as a fact. For the same reason AI 8043 has no
 * content validator — see {@code EidValidator} for the contrasting AI 8042 case.
 *
 * <p>The component split is taken from the parsed component values rather than by
 * re-slicing the raw string, so it always agrees with the tokeniser.
 *
 * <p>Stateless and thread-safe, as {@link InterpretationEnricherInterface} requires.
 */
public final class IccidEnricher implements InterpretationEnricherInterface {

    /** Length of the mandatory first component of AI 8043 ({@code N18}). */
    private static final int BODY_LENGTH = 18;

    /** Major Industry Identifier length (ISO/IEC 7812). */
    private static final int MII_END = 2;

    public IccidEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return Collections.emptyList();

        String body = components.get(0).getValue();
        if (body == null || body.length() != BODY_LENGTH || !StringUtils.isAllDigits(body)) {
            return Collections.emptyList();
        }

        List<GS1AIInterpretation> out = new ArrayList<>(3);
        out.add(new GS1AIInterpretation(GS1Constants_Enricher.SIM_MII, null, body.substring(0, MII_END)));
        out.add(new GS1AIInterpretation(GS1Constants_Enricher.ICCID_BODY, null, body.substring(MII_END)));

        // The optional [N1..N2] component is present only when the value carried it.
        if (components.size() > 1) {
            String extension = components.get(1).getValue();
            if (extension != null && !extension.isEmpty()) {
                out.add(new GS1AIInterpretation(GS1Constants_Enricher.ICCID_EXTENSION, null, extension));
            }
        }

        return List.copyOf(out);
    }
}
