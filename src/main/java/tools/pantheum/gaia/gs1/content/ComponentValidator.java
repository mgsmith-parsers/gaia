package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.util.ASCIIRevealerUtils;
import tools.pantheum.gaia.gs1.charset.GS1CharacterSet;

import java.util.Map;

/**
 * Content-stage validator that checks each pre-sliced {@link GS1AIComponentValue} on a
 * {@link GS1AIObjectElement}.
 *
 * <p>Runs after {@link RegexValidator} so structural format (digit count, overall
 * pattern) has already been verified. The component slices are produced by
 * {@link tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser} during Phase 2 and stored
 * on the element — this validator does not re-slice the raw value.
 *
 * <p>For each component this validator applies two checks in order:
 * <ol>
 *   <li><b>Charset</b> ({@code GE-C005}) — confirms every character in the component
 *       slice belongs to the type's permitted set:
 *       <ul>
 *         <li>{@code "N"} — digits {@code 0–9} only (NUMERIC)</li>
 *         <li>{@code "X"} — GS1 AI encodable character set 82 (CSET82), narrowed to
 *             CSET64 or CSET39 when the AI format string indicates it</li>
 *       </ul>
 *   </li>
 *   <li><b>Format</b> — applies a format-specific semantic rule (e.g. date validity,
 *       range check, structural pattern) via {@link FormatValidators}; each format
 *       validator raises its own condition-specific error code. This check is skipped
 *       if the charset check produced any errors for this component.
 *   </li>
 * </ol>
 */
public final class ComponentValidator {

    public static final ComponentValidator INSTANCE = new ComponentValidator();

    private ComponentValidator() {}

    /**
     * Validates each component slice of {@code element} and attaches any charset
     * ({@code GE-C005}) or per-condition format errors directly to the element.
     *
     * @param element the parsed AI element to validate
     * @param config  parse configuration — controls the language of error messages
     */
    public void validate(GS1AIObjectElement element, ParseConfig config) {
        for (GS1AIComponentValue cv : element.getGS1ComponentValues()) {

            // --- Step 1: Charset validation (GE-C005) ---
            int errorsBefore = element.getErrors().size();
            GS1CharacterSet charSet = GS1CharacterSet.forComponentType(cv.getType(), element.getFormatString());
            if (charSet != null) {
                validateCharset(element, cv, charSet, config);
            }

            // --- Step 2: Format validation (per-condition codes) ---
            // Only run if the charset check introduced no new errors for this component.
            if (element.getErrors().size() == errorsBefore) {
                String format = cv.getFormat();
                if (format != null) {
                    ComponentFormatInterface fmtValidator = FormatValidators.forFormat(format);
                    if (fmtValidator != null) {
                        fmtValidator.validate(cv.getValue(), element.getAi(), element.getPosition(),
                                cv.getLabel(), format, config.getLanguage()).forEach(element::addError);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------

    private void validateCharset(GS1AIObjectElement element, GS1AIComponentValue cv,
                                 GS1CharacterSet charSet, ParseConfig config) {
        String slice = cv.getValue();
        for (int i = 0; i < slice.length(); i++) {
            char c = slice.charAt(i);
            if (charSet.contains(c)) continue;

            String display        = (c <= 32) ? "{" + ASCIIRevealerUtils.nameOf(c) + "}" : String.valueOf(c);
            int    absoluteOffset = cv.getOffset() + i;

            element.addError(ErrorRegistry.INSTANCE.create("GE-C005", element.getAi(), element.getPosition(),
                    Map.of(
                            "character", display,
                            "offset",    String.valueOf(absoluteOffset),
                            "ai",        element.getAi(),
                            "component", cv.getLabel(),
                            "charset",   charSet.name()
                    ), config.getLanguage()));
        }
    }
}
