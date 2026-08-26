package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.PcEncUtils;

import java.util.Collections;
import java.util.List;

/**
 * Enriches GS1 {@code pcenc} (percent-encoded) text fields by decoding
 * any percent-encoded UTF-8 byte sequences into a plain Unicode string.
 *
 * <p>GS1 uses percent-encoding (as defined in RFC 3986) to allow non-ASCII
 * characters in element strings. Multi-byte UTF-8 characters are encoded as
 * consecutive {@code %XX} sequences, e.g. the Euro sign {@code €} becomes
 * {@code %E2%82%AC} and the accented character {@code é} becomes {@code %C3%A9}.
 *
 * <p>Applied to all {@code pcenc} AIs, including ship-to/return-to address
 * fields (4300–4316, 4320) and the healthcare person name/address fields
 * 7253, 7254, 7256, 7257 and 7259. The remaining 725x AIs are not
 * {@code pcenc} — 7255 (SUFFIX) is a plain {@code X..10} and 7258
 * (BIRTH SEQUENCE) is {@code posinseqslash} — so neither is enriched here.
 *
 * <h2>Plus-sign handling</h2>
 * In every {@code pcenc} AI a space character is encoded as a single {@code +}.
 * This convention is applied <em>after</em> RFC 3986 percent-decoding, so every
 * {@code +} in the decoded string becomes a space — including one that came from
 * a {@code %2B}. A literal {@code +} is therefore not representable in a
 * {@code pcenc} field.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code DECODED_TEXT} — the percent-decoded Unicode string,
 *       e.g. input {@code "Fran%C3%A7ois"} → {@code "François"};
 *       {@code "O+Brien"} → {@code "O Brien"}.
 *       If the value contains no percent-encoding and no plus sign the decoded
 *       value matches the raw value.</li>
 * </ul>
 * Returns an empty list if the value is null or empty.
 */
public final class PcEncEnricher implements InterpretationEnricherInterface {

    public PcEncEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        String decoded = decode(baseValue);
        return List.of(new GS1AIInterpretation(GS1Constants_Enricher.DECODED_TEXT, null, decoded));
    }

    /**
     * Percent-decodes {@code value} as UTF-8 and maps every {@code +} in the
     * result to a space, returning the raw value unchanged if it contains a
     * malformed percent-encoded sequence (lenient — a bad value must not abort
     * interpretation).
     *
     * @param value the raw pcenc value
     */
    static String decode(String value) {
        try {
            // Apply the GS1 plus-as-space convention only after decoding, so it maps the
            // field's own '+' characters (a percent-encoded space, %20, is already a space).
            return PcEncUtils.decode(value).replace("+", " ");
        } catch (IllegalArgumentException e) {
            // Malformed percent-encoding — return raw value as-is
            return value;
        }
    }
}
