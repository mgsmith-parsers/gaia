package tools.pantheum.gaia.modifier.custom;

import tools.pantheum.gaia.correlation.CorrelationIdParser;
import tools.pantheum.gaia.datacarrier.DataCarrierParser;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierRegistry;
import tools.pantheum.gaia.modifier.ModifierInterface;

import java.util.Optional;

/**
 * Removes every space character from a GS1 AI element string.
 *
 * <p>Some scanners, middleware, and label-print pipelines insert spurious {@code ' '}
 * (0x20) characters into an otherwise well-formed element string — padding a
 * fixed-width field, separating human-readable groups, or wrapping a long value. The
 * GS1 tokeniser treats those spaces as data, so they corrupt the value they sit in
 * and, for a variable-length AI, shift everything after them. This modifier strips
 * them out before parsing begins.
 *
 * <pre>
 *   in   0109506000134352 21 SER 123     (spaces inserted by an upstream system)
 *   out  010950600013435221SER123        (spaces removed, element string resolves)
 * </pre>
 *
 * <h2>Prefixes are skipped, not stripped</h2>
 * Modifiers run at the very start of {@link tools.pantheum.gaia.GaiaParser}, before the
 * parser has stripped anything, so the raw input may still carry any combination of:
 *
 * <ol>
 *   <li>an 8-digit correlation ID and {@code ~} — e.g. {@code 12345678~}</li>
 *   <li>a three-character AIM Code ID — e.g. {@code ]C1}</li>
 *   <li>a seven-character ECI indicator ({@code '\'} + six digits), when the carrier
 *       is ECI-capable — e.g. {@code \000004}</li>
 * </ol>
 *
 * Only the AI element string is de-spaced: this modifier locates the end of those
 * prefixes using the parser's own {@link CorrelationIdParser} and
 * {@link DataCarrierParser} logic and removes spaces from there onward, splicing the
 * result back onto the untouched prefix. Every prefix is preserved verbatim, so
 * {@code 12345678~]C1\0000040109506000134352 21 SER123} has only the payload spaces
 * removed.
 *
 * <p>EAN/UPC carriers that require GTIN padding are skipped: their payload is a raw
 * numeric barcode value with no AI structure, and a legitimate value never contains a
 * space.
 *
 * <h2>Limitation — space is a valid GS1 character</h2>
 * The space character (0x20) is part of the GS1 invariant character set, so a value
 * such as a batch/lot or a customer part number may legitimately contain a space.
 * This modifier cannot tell a spurious space from a genuine one; apply it only to a
 * source known not to use spaces inside its AI values.
 *
 * <p>Stateless and thread-safe, as {@link ModifierInterface} requires.
 */
public final class ModifierRemoveSpaces implements ModifierInterface {

    /** The character removed from the AI element string. */
    private static final String SPACE = " ";

    /** First character of an ECI indicator sequence. */
    private static final char ECI_INDICATOR_LEAD = '\\';

    private static final CorrelationIdParser CORRELATION = new CorrelationIdParser();

    @Override
    public String modify(String input) {
        if (input == null || input.isEmpty()) return input;

        int offset = aiPayloadOffset(input);
        if (offset < 0) return input;   // carrier carries no AI element string

        String payload  = input.substring(offset);
        String stripped = payload.replace(SPACE, "");
        if (stripped.equals(payload)) return input;   // no spaces to remove

        return input.substring(0, offset) + stripped;
    }

    /**
     * Returns the index at which the GS1 AI element string begins — i.e. the length of the
     * correlation, AIM Code ID, and ECI prefixes that {@link tools.pantheum.gaia.GaiaParser}
     * will strip before the AI parser sees the payload.
     *
     * @return the offset of the first AI character, or {@code -1} when the carrier does not
     *         carry an AI element string at all (an EAN/UPC payload requiring GTIN padding)
     */
    private static int aiPayloadOffset(String input) {
        // 1. Correlation ID prefix (DDDDDDDD~), if present.
        int offset = input.length() - CORRELATION.parse(input).getStrippedPayload().length();
        String payload = input.substring(offset);

        // 2. AIM Code ID (']' + letter + digit), if present.
        if (!DataCarrierParser.startsWithDataCarrier(payload)) {
            return offset;
        }
        String aimCodeId = payload.substring(0, DataCarrierParser.AIM_CODE_ID_LENGTH);
        offset  += DataCarrierParser.AIM_CODE_ID_LENGTH;
        payload  = payload.substring(DataCarrierParser.AIM_CODE_ID_LENGTH);

        Optional<DataCarrierEntry> entry = DataCarrierRegistry.forAimCodeId(aimCodeId);
        if (!entry.isPresent()) {
            return offset;   // unknown carrier — the parser will reject it; leave the payload alone
        }
        // An EAN/UPC payload is a raw numeric value, not an AI element string.
        if (entry.get().isRequiresGtinPadding()) {
            return -1;
        }

        // 3. ECI indicator ('\' + six digits), only on an ECI-capable carrier.
        if (entry.get().isEciCapable() && hasEciIndicator(payload)) {
            offset += DataCarrierParser.ECI_INDICATOR_LENGTH;
        }
        return offset;
    }

    /** Mirrors the parser's ECI detection: a backslash followed by exactly six digits. */
    private static boolean hasEciIndicator(String payload) {
        if (payload.length() < DataCarrierParser.ECI_INDICATOR_LENGTH) return false;
        if (payload.charAt(0) != ECI_INDICATOR_LEAD) return false;
        for (int i = 1; i < DataCarrierParser.ECI_INDICATOR_LENGTH; i++) {
            if (!Character.isDigit(payload.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Returns {@code "Remove Space Characters"} — the name reported in
     * {@link tools.pantheum.gaia.modifier.ModifierInfo#getAppliedModifiers()} and in the
     * HTML reports, in place of the default simple class name.
     */
    @Override
    public String getName() {
        return "Remove Space Characters";
    }
}
