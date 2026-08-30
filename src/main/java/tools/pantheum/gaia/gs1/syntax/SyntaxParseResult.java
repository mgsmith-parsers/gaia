package tools.pantheum.gaia.gs1.syntax;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1DigitalLinkInfo;

import java.util.List;

/**
 * The output of a syntax-stage parser: the tokenised AI elements and any
 * syntax-level errors collected during Stage 1 parsing.
 *
 * <p>Produced by {@link tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser} for plain element strings and by
 * {@link tools.pantheum.gaia.gs1.syntax.digitallink.DLSyntaxParser} for GS1 Digital
 * Link URIs. For Digital Link inputs the result additionally carries the
 * decomposed URL metadata ({@link #getDigitalLinkInfo()}); for element strings
 * — or when the input could not be converted to a URL at all — it is {@code null}.
 *
 * <p>Object-level errors in this result are tokeniser {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#SYNTAX_ERROR}s
 * and {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#WARNING}s (e.g. GE-S001–S003,
 * GE-S007–S011, GE-W002), plus Digital Link structural errors ({@code GE-L001}–{@code GE-L012})
 * when the input was a Digital Link URI. Structural {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#INTEGRITY_ERROR}s
 * from {@link SyntaxValidator} (GE-S004–S006: duplicate AI, missing dependency, excluded pairing)
 * are attached directly to the offending {@link tools.pantheum.gaia.gs1.model.GS1AIObjectElement}
 * and are accessible via {@link #hasSyntaxErrors()}.
 */
public class SyntaxParseResult {

    private final List<GS1AIObjectElement> elements;
    private final List<GaiaError>          errors;
    private final GS1DigitalLinkInfo       digitalLinkInfo;

    /**
     * Result of element-string parsing — no Digital Link metadata.
     *
     * @param elements the elements
     * @param errors the errors
     */
    public SyntaxParseResult(List<GS1AIObjectElement> elements, List<GaiaError> errors) {
        this(elements, errors, null);
    }

    /**
     * Result of Digital Link parsing.
     *
     * @param elements        the tokenised AI elements
     * @param errors          syntax-stage errors
     * @param digitalLinkInfo the decomposed URL metadata, or {@code null} when
     *                        the input was not a valid URL
     */
    public SyntaxParseResult(List<GS1AIObjectElement> elements, List<GaiaError> errors,
                             GS1DigitalLinkInfo digitalLinkInfo) {
        this.elements        = elements;
        this.errors          = errors;
        this.digitalLinkInfo = digitalLinkInfo;
    }

    /**
     * Returns the elements.
     *
     * @return the elements.
     */
    public List<GS1AIObjectElement> getElements() { return elements; }
    /**
     * Returns the errors.
     *
     * @return the errors.
     */
    public List<GaiaError>          getErrors()   { return errors; }

    /**
     * The decomposed URL metadata when the input was a GS1 Digital Link URI,
     * or {@code null} for plain element strings and unconvertible URLs.
     *
     * @return the digital link info.
     */
    public GS1DigitalLinkInfo getDigitalLinkInfo() { return digitalLinkInfo; }

    /**
     * Returns {@code true} if the input was a GS1 Digital Link URI with a valid URL.
     *
     * @return {@code true} if this element has digital link.
     */
    public boolean hasDigitalLink() { return digitalLinkInfo != null; }

    /**
     * True when any error from Stage 1 was recorded — includes tokeniser
     * SYNTAX_ERRORs (GE-S001–S003, GE-S007–S011), Digital Link structural
     * errors (GE-L001–GE-L012), and structural INTEGRITY_ERRORs attached to elements
     * (GE-S004–S006: duplicate AI, missing dependency, excluded pairing).
     * WARNINGs are not included.
     *
     * @return {@code true} if this element has syntax errors.
     */
    public boolean hasSyntaxErrors() {
        return errors.stream().anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.SYNTAX_ERROR)
            || elements.stream().anyMatch(GS1AIObjectElement::hasErrors);
    }
}
