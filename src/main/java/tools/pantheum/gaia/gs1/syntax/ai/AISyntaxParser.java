package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiComponent;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.localization.AiDescriptionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tokenises a raw GS1 element string into a list of {@link GS1AIObjectElement}
 * objects.
 *
 * <p>Parsing rules (GS1 General Specifications Release 26.0, Ratified, Jan 26 section 7.8):
 * <ul>
 *   <li>The first two characters of an AI determine the total AI code length (Table 7-5).</li>
 *   <li>AIs whose {@code separatorRequired} is {@code false} have a predefined data length
 *       derived from their component definitions (Table 7-6). No separator is consumed.</li>
 *   <li>Variable-length AIs are terminated by the GS character (ASCII 0x1D) or end-of-string.</li>
 *   <li>A trailing GS after any element string is tolerated and consumed (spec §7.8.6.3).</li>
 * </ul>
 *
 * <p>Parsing is two-phase:
 * <ol>
 *   <li><b>Phase 1 – Read:</b> extract the full value blob from the input stream using
 *       fixed-length or GS-terminated variable-length rules.</li>
 *   <li><b>Phase 2 – Component walk:</b> for AIs with more than one component, walk the
 *       component list left-to-right across the blob, consuming fixed-length components by
 *       exact length and variable-length components up to their declared maximum. Optional
 *       components are skipped when the value is exhausted. Trailing data beyond all
 *       component maximums produces a {@code GE-S010} error.</li>
 * </ol>
 *
 * On the first un-recoverable {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#SYNTAX_ERROR}
 * parsing stops. All elements successfully tokenised before the error are still returned
 * so that callers can report as much context as possible.
 */
public class AISyntaxParser {

    private final AiDefinitionRegistry registry;
    private final AICharacterSetChecker  charsetChecker;

    /**
     * Creates a new {@link AISyntaxParser}.
     *
     * @param registry the registry
     */
    public AISyntaxParser(AiDefinitionRegistry registry) {
        this.registry       = registry;
        this.charsetChecker = new AICharacterSetChecker();
    }

    /**
     * Parse.
     *
     * @param input the input
     * @param config the config
     * @return the parse.
     */
    public SyntaxParseResult parse(String input, ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        List<GS1AIObjectElement> elements = new ArrayList<>();
        List<GaiaError>          errors   = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return new SyntaxParseResult(elements, errors);
        }

        // Pre-flight: reject any character outside CSET82 / FNC1-GS before tokenising
        errors.addAll(charsetChecker.checkInput(input, config));
        if (!errors.isEmpty()) {
            return new SyntaxParseResult(elements, errors);
        }

        try {
            int pos = 0;
            while (pos < input.length()) {

                int elementStart = pos;

                // ---- 1. Determine AI code length from first two characters ----
                if (pos + 2 > input.length()) {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S002", null, pos,
                            Map.of("position", String.valueOf(pos)), lang));
                    break;
                }

                String prefix = input.substring(pos, pos + 2);
                int aiLen = AiDefinitionRegistry.aiLengthForPrefix(prefix);
                if (aiLen < 0) {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S001", prefix, pos,
                            Map.of("ai", prefix, "position", String.valueOf(pos)), lang));
                    break; // cannot recover: unknown prefix means unknown data length
                }

                if (pos + aiLen > input.length()) {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S002", prefix, pos,
                            Map.of("position", String.valueOf(pos)), lang));
                    break;
                }

                String aiCode = input.substring(pos, pos + aiLen);
                pos += aiLen;

                // ---- 2. Look up the definition ----
                Optional<AiDefinition> defOpt = registry.find(aiCode);
                if (defOpt.isEmpty()) {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S001", aiCode, elementStart,
                            Map.of("ai", aiCode, "position", String.valueOf(elementStart)), lang));
                    break; // cannot recover: data length unknown
                }
                AiDefinition def = defOpt.get();

                // ---- 3. Phase 1: read the full value blob ----
                String value;
                // Fixed Length and No Separator Required
                if (def.isFixedLength()) {
                    int dataLen = def.getFixedDataLength();
                    if (pos + dataLen > input.length()) {
                        errors.add(ErrorRegistry.INSTANCE.create("GE-S003", aiCode, elementStart,
                                Map.of("ai", aiCode, "position", String.valueOf(elementStart)), lang));
                        break;
                    }
                    value = input.substring(pos, pos + dataLen);
                    pos += dataLen;
                    if (pos < input.length() && input.charAt(pos) == GS1Constants.FNC1_GS) {
                        if (pos == input.length() - 1) {
                            // FNC1 is the last character — trailing separator, advisory only
                            // (consistent with the variable-length trailing-FNC1 handling above)
                            errors.add(ErrorRegistry.INSTANCE.create("GE-W002", aiCode, pos,
                                    Map.of("ai", aiCode), lang));
                        } else {
                            // FNC1 in the middle after a fixed-length AI — subsequent AIs
                            // would be mis-parsed because fixed-length AIs consume an exact
                            // byte count and cannot use a separator to delimit their value.
                            errors.add(ErrorRegistry.INSTANCE.create("GE-S011", aiCode, pos,
                                    Map.of("ai", aiCode, "position", String.valueOf(pos)), lang));
                            break;
                        }
                    }
                } else {
                    // Variable-length: read until FNC1 or end of input
                    int gsPos = input.indexOf(GS1Constants.FNC1_GS, pos);

                    if (gsPos != -1) {
                        // FNC1 found — use it as the terminator
                        value = input.substring(pos, gsPos);
                        pos = gsPos + 1; // consume FNC1
                        if (gsPos == input.length() - 1) {
                            // FNC1 is the last character — trailing separator, advisory only
                            errors.add(ErrorRegistry.INSTANCE.create("GE-W002", aiCode, gsPos,
                                    Map.of("ai", aiCode), lang));
                        }
                    } else {
                        // No FNC1 — read to end of input.
                        // A separator-required AI that is the last element in the string
                        // legitimately has no trailing FNC1 (GS1 spec §7.8.6). GE-S009 is
                        // intentionally not emitted here: the parser cannot distinguish a
                        // correctly-terminated last element from one that absorbed subsequent
                        // AIs, because in both cases all remaining input has been consumed.
                        value = input.substring(pos);
                        pos = input.length();
                    }
                }

                // ---- 4. Phase 2: walk components to build per-component slices ----
                List<GS1AIComponentValue> componentValues;
                if (def.getComponents().size() > 1) {
                    AIComponentWalkResult walk = walkComponents(def, value, aiCode, elementStart, config);
                    errors.addAll(walk.errors);
                    componentValues = walk.componentValues;
                } else {
                    // Single-component AI: the entire value is that one component
                    componentValues = List.of(new GS1AIComponentValue(def.getComponents().get(0), value, 0));
                }

                String description = AiDescriptionRegistry.INSTANCE.descriptionFor(aiCode, lang);
                if (description == null) description = def.getDescription();
                elements.add(new GS1AIObjectElement(def, value, elementStart, componentValues, description));
            }
        } catch (RuntimeException e) {
            System.err.println("[AISyntaxParser] Unexpected error during tokenisation: " + e);
            errors.add(ErrorRegistry.INSTANCE.create("GE-S007", null, 0,
                    Map.of("message", e.getClass().getSimpleName() + ": " + e.getMessage()), lang));
        }

        return new SyntaxParseResult(elements, errors);
    }

    /**
     * Phase 2 — walks the component list left-to-right across the extracted value blob,
     * slicing it into {@link GS1AIComponentValue} instances and validating structural lengths.
     *
     * <ul>
     *   <li>Fixed-length components consume exactly {@code component.getLength()} characters.</li>
     *   <li>Variable-length components consume the remaining characters up to
     *       {@code component.getLength()} (their declared maximum).</li>
     *   <li>Optional components are silently skipped when the value is exhausted.</li>
     *   <li>Any characters left in the blob after all components are satisfied produce
     *       a {@code GE-S010} error.</li>
     * </ul>
     *
     * @return a {@link AIComponentWalkResult} containing any structural errors and the
     *         list of successfully sliced {@link GS1AIComponentValue} entries
     */
    private AIComponentWalkResult walkComponents(AiDefinition def, String value,
                                               String aiCode, int elementStart,
                                               ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        List<GaiaError>           errors          = new ArrayList<>();
        List<GS1AIComponentValue> componentValues = new ArrayList<>();
        int offset = 0;

        for (AiComponent comp : def.getComponents()) {
            int remaining = value.length() - offset;

            if (remaining == 0) {
                if (comp.isOptional()) {
                    break; // optional and value exhausted — acceptable
                } else {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S003", aiCode, elementStart,
                            Map.of("ai", aiCode, "position", String.valueOf(elementStart + offset)), lang));
                    break;
                }
            }

            if (comp.isFixedLength()) {
                if (remaining < comp.getLength()) {
                    errors.add(ErrorRegistry.INSTANCE.create("GE-S003", aiCode, elementStart,
                            Map.of("ai", aiCode, "position", String.valueOf(elementStart + offset)), lang));
                    break;
                }
                String slice = value.substring(offset, offset + comp.getLength());
                componentValues.add(new GS1AIComponentValue(comp, slice, offset));
                offset += comp.getLength();
            } else {
                // Variable-length: consume up to the component's declared maximum
                int consumed = Math.min(remaining, comp.getLength());
                String slice = value.substring(offset, offset + consumed);
                componentValues.add(new GS1AIComponentValue(comp, slice, offset));
                offset += consumed;
            }
        }

        // Any characters beyond all component maximums are unexpected
        int trailing = value.length() - offset;
        if (trailing > 0) {
            errors.add(ErrorRegistry.INSTANCE.create("GE-S010", aiCode, elementStart,
                    Map.of("ai", aiCode, "trailing", String.valueOf(trailing)), lang));
        }

        return new AIComponentWalkResult(errors, componentValues);
    }

}
