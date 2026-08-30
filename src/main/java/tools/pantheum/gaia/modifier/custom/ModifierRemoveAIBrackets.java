package tools.pantheum.gaia.modifier.custom;

import tools.pantheum.gaia.correlation.CorrelationIdParser;
import tools.pantheum.gaia.datacarrier.DataCarrierParser;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierRegistry;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.modifier.ModifierInterface;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes the parentheses an upstream system prints around each Application Identifier,
 * leaving the bare AIs the GS1 tokeniser expects.
 *
 * <p>The GS1 human-readable interpretation (HRI) wraps every Application Identifier in
 * parentheses — {@code (01)...(10)...(17)...} — purely as a printing convention. Those
 * parentheses are <em>not</em> part of the encoded element string, but a scanner or
 * middleware configured to emit the HRI passes them through as data. This modifier
 * strips the brackets from every bracketed AI, so the payload parses as a plain
 * element string.
 *
 * <pre>
 *   in   (400)1234A1234567899(90)DD123        (HRI brackets emitted as data)
 *   out  4001234A1234567899&lt;GS&gt;90DD123        (bare AIs — element string resolves)
 * </pre>
 *
 * <p>An AI code is two to four digits, so a bracketed AI is matched as {@code '('} + two
 * to four digits + {@code ')'} and replaced with the digits alone.
 *
 * <h2>The separator the brackets used to imply</h2>
 * In HRI the opening {@code '('} of the next AI is what marks the end of the previous
 * value, so a variable-length AI needs no FNC1 in bracketed form. Stripping the brackets
 * removes that boundary: {@code (400)1234A1234567899(90)DD123} would collapse to
 * {@code 4001234A123456789990DD123}, where AI (400) — {@code N3+X..30}, and therefore
 * {@code separatorRequired} — swallows the rest of the string.
 *
 * <p>This modifier therefore inserts an FNC1 at each bracket boundary whose preceding AI
 * is variable-length, restoring exactly what the brackets encoded. The AI is looked up in
 * the parser's own {@link AiDefinitionRegistry}, so every variable-length AI is handled,
 * not a fixed list: a fixed-length AI such as (01) is left unseparated, and no separator
 * is added after the final AI, whose value ends at the end of the string.
 *
 * <p>A boundary is left alone when the value already ends with an FNC1 (a source that
 * emits both conventions is not given a second separator) and when the bracketed AI is
 * not in the registry, as an unknown AI states nothing about its own length — the parser
 * rejects it either way.
 *
 * <h2>Prefixes are skipped, not matched against</h2>
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
 * The brackets are removed from the AI element string only: this modifier locates the
 * end of those prefixes using the parser's own {@link CorrelationIdParser} and
 * {@link DataCarrierParser} logic, strips the brackets from there onward, and splices
 * the result back onto the untouched prefix. Every prefix is preserved verbatim.
 *
 * <p>EAN/UPC carriers that require GTIN padding are skipped: their payload is a raw
 * numeric barcode value with no AI structure and no HRI parentheses.
 *
 * <h2>Limitation — a parenthesised number is assumed to be an AI</h2>
 * The pattern rewrites any {@code '('} + two-to-four-digit + {@code ')'} sequence, since
 * {@code (} and {@code )} are themselves valid GS1 data characters, a value that
 * happens to contain such a sequence would also have it unwrapped. Apply this modifier
 * only to a source whose payload uses the HRI bracket convention rather than genuine
 * parenthesised values.
 *
 * <p>Stateless and thread-safe, as {@link ModifierInterface} requires.
 */
public final class ModifierRemoveAIBrackets implements ModifierInterface {

    /** Creates a new {@link ModifierRemoveAIBrackets}. */
    public ModifierRemoveAIBrackets() {}

    /**
     * A bracketed Application Identifier in HRI form: {@code '('} + a two-to-four-digit AI
     * code + {@code ')'}. Group 1 captures the bare AI code the brackets are replaced with.
     */
    public static final String PATTERN_SOURCE = "\\((\\d{2,4})\\)";

    private static final Pattern PATTERN = Pattern.compile(PATTERN_SOURCE);

    /** First character of an ECI indicator sequence. */
    private static final char ECI_INDICATOR_LEAD = '\\';

    private static final char FNC1 = GS1Constants.FNC1_GS;

    private static final CorrelationIdParser CORRELATION = new CorrelationIdParser();

    private static final AiDefinitionRegistry AI_REGISTRY = AiDefinitionRegistry.getInstance();

    @Override
    public String modify(String input) {
        if (input == null || input.isEmpty()) return input;

        int offset = aiPayloadOffset(input);
        if (offset < 0) return input;   // carrier carries no AI element string

        String payload = input.substring(offset);
        Matcher matcher = PATTERN.matcher(payload);
        if (!matcher.find()) return input;   // no brackets to remove

        StringBuilder stripped = new StringBuilder(payload.length());
        int cursor = 0;
        String precedingAi = null;   // the AI whose value runs up to the next bracket

        do {
            // The text since the last bracketed AI is that AI's value.
            String value = payload.substring(cursor, matcher.start());
            stripped.append(value);
            if (separatorRequiredAfter(precedingAi, value)) stripped.append(FNC1);

            stripped.append(matcher.group(1));
            precedingAi = matcher.group(1);
            cursor      = matcher.end();
        } while (matcher.find());

        // The trailing value ends at the end of the string, so it needs no separator.
        stripped.append(payload, cursor, payload.length());

        return input.substring(0, offset) + stripped;
    }

    /**
     * Returns {@code true} when the value just emitted for {@code aiCode} must be closed with
     * an FNC1 before the AI that follows it — i.e. when that AI is variable-length, and the
     * value is not already separated.
     *
     * @param aiCode the AI whose value this is, or {@code null} for text preceding the first
     *               bracketed AI, whose owning AI is unknown
     */
    private static boolean separatorRequiredAfter(String aiCode, String value) {
        if (aiCode == null) return false;
        // Already delimited — a source emitting both conventions gets no second separator.
        if (!value.isEmpty() && value.charAt(value.length() - 1) == FNC1) return false;

        Optional<AiDefinition> definition = AI_REGISTRY.find(aiCode);
        return definition.isPresent() && definition.get().isSeparatorRequired();
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
     * Returns {@code "Remove Brackets Around AI"} — the name reported in
     * {@link tools.pantheum.gaia.modifier.ModifierInfo#getAppliedModifiers()} and in the
     * HTML reports, in place of the default simple class name.
     */
    @Override
    public String getName() {
        return "Remove Brackets Around AI";
    }
}
