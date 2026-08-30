package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.util.PcEncUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of parsing GS1 content — either a plain element string or a
 * GS1 Digital Link URI — holding all resolved {@link GS1AIObjectElement} instances
 * and any object-level validation errors.
 *
 * <p>Both the element list and the error list are mutable after construction.
 * New elements are appended via {@link #add(GS1AIObjectElement)} and new
 * object-level errors via {@link #addError(GaiaError)}. Public accessors
 * always return defensive unmodifiable views.
 *
 * <p>Validity is determined by {@link #isValid()}: the object is valid when
 * there are no object-level non-WARNING errors and no element has attached
 * errors. {@link GaiaConstants.ErrorLevel#WARNING} advisories do not affect
 * validity.
 */
public class GS1AIObject {

    private final List<GS1AIObjectElement> ais;
    private final List<GaiaError>          errors;
    private final GS1DigitalLinkInfo       digitalLinkInfo;
    private GS1Constants.ParseMode         achievedParseMode;

    /**
     * Creates a new {@link GS1AIObject}.
     *
     * @param elements the elements
     * @param errors the errors
     */
    public GS1AIObject(List<GS1AIObjectElement> elements, List<GaiaError> errors) {
        this(elements, errors, null);
    }

    /**
     * The deepest pipeline stage actually reached during parsing
     * ({@link GS1Constants.ParseMode#SYNTAX SYNTAX}, {@link GS1Constants.ParseMode#CONTENT CONTENT},
     * or {@link GS1Constants.ParseMode#INTERPRETATION INTERPRETATION}). This may be shallower than
     * the requested mode when the pipeline stops early on errors. Set by the parsing pipeline;
     * {@code null} if not produced by the pipeline.
     *
     * @return the achieved parse mode.
     */
    public GS1Constants.ParseMode getAchievedParseMode() { return achievedParseMode; }

    /**
     * Records the deepest pipeline stage reached. Called by the parsing pipeline.
     *
     * @param mode the mode
     */
    public void setAchievedParseMode(GS1Constants.ParseMode mode) { this.achievedParseMode = mode; }

    /**
     * Creates a new {@link GS1AIObject}.
     *
     * @param elements the elements
     * @param errors the errors
     * @param digitalLinkInfo the digital link info
     */
    public GS1AIObject(List<GS1AIObjectElement> elements, List<GaiaError> errors,
                       GS1DigitalLinkInfo digitalLinkInfo) {
        this.ais             = new ArrayList<>(elements);
        this.errors          = new ArrayList<>(errors);
        this.digitalLinkInfo = digitalLinkInfo;
    }

    /**
     * Adds a {@link GS1AIObjectElement} to this object.
     *
     * @param element the element to add; must not be {@code null}
     */
    public void add(GS1AIObjectElement element) {
        if (element == null) throw new IllegalArgumentException("element must not be null");
        ais.add(element);
    }

    /**
     * Adds a {@link GaiaError} to this object.
     *
     * @param error the error to add; must not be {@code null}
     */
    public void addError(GaiaError error) {
        if (error == null) throw new IllegalArgumentException("error must not be null");
        errors.add(error);
    }

    /**
     * Metadata extracted from a GS1 Digital Link URI, or {@code null} if the input
     * was not a Digital Link.
     *
     * @return the digital link info.
     */
    public GS1DigitalLinkInfo getDigitalLinkInfo() { return digitalLinkInfo; }

    /**
     * Returns {@code true} if the input was a GS1 Digital Link URI carrying at
     * least one primary identification key. The URL metadata may still be present
     * (see {@link #getDigitalLinkInfo()}) for a well-formed URL that lacks a
     * primary key — such an input is not a usable Digital Link.
     *
     * @return {@code true} if this element has digital link.
     */
    public boolean hasDigitalLink() {
        return digitalLinkInfo != null
                && ais.stream().anyMatch(e ->
                        e.getDigitalLinkAIType() == GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY);
    }

    /**
     * Returns the {@link GS1Constants.GS1ContentType} of the parsed content.
     *
     * <ul>
     *   <li>{@link GS1Constants.GS1ContentType#GS1_DIGITAL_LINK} — when
     *       {@link #hasDigitalLink()} is {@code true}.</li>
     *   <li>{@link GS1Constants.GS1ContentType#GS1_APPLICATION_IDENTIFIERS} — otherwise.</li>
     * </ul>
     *
     * @return the content type.
     */
    public GS1Constants.GS1ContentType getContentType() {
        return hasDigitalLink()
                ? GS1Constants.GS1ContentType.GS1_DIGITAL_LINK
                : GS1Constants.GS1ContentType.GS1_APPLICATION_IDENTIFIERS;
    }

    /**
     * All resolved AIs in input order.
     *
     * @return the AIs.
     */
    public List<GS1AIObjectElement> getAis() { return Collections.unmodifiableList(ais); }

    /**
     * Returns the first AI matching the given code, or {@code null} if not present.
     *
     * @param aiCode the AI code
     * @return the .
     */
    public GS1AIObjectElement get(String aiCode) {
        return ais.stream()
                .filter(a -> a.getAi().equals(aiCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns {@code true} if an AI with the given code is present.
     *
     * @param aiCode the AI code
     * @return {@code true} if contains.
     */
    public boolean contains(String aiCode) {
        return ais.stream().anyMatch(a -> a.getAi().equals(aiCode));
    }

    /**
     * Object-level non-WARNING errors produced during parsing.
     * Element-level errors (including structural integrity errors) are held on individual
     * elements — use {@link #getAllErrors()} to collect errors from all levels.
     * Warnings are excluded; use {@link #getWarnings()} to retrieve those.
     *
     * @return the errors.
     */
    public List<GaiaError> getErrors() {
        return errors.stream()
                .filter(e -> e.getLevel() != GaiaConstants.ErrorLevel.WARNING)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Object-level {@link GaiaConstants.ErrorLevel#WARNING} advisories.
     *
     * @return the warnings.
     */
    public List<GaiaError> getWarnings() {
        return errors.stream()
                .filter(e -> e.getLevel() == GaiaConstants.ErrorLevel.WARNING)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * All non-WARNING issues from both the object level and every element, in order:
     * object-level errors first, then each element's errors in element order.
     *
     * @return the all errors.
     */
    public List<GaiaError> getAllErrors() {
        List<GaiaError> all = new ArrayList<>(getErrors());
        ais.forEach(e -> all.addAll(e.getErrors()));
        return Collections.unmodifiableList(all);
    }

    /**
     * All {@link GaiaConstants.ErrorLevel#WARNING} advisories from both the object level and every element.
     *
     * @return the all warnings.
     */
    public List<GaiaError> getAllWarnings() {
        List<GaiaError> all = new ArrayList<>(getWarnings());
        ais.forEach(e -> all.addAll(e.getWarnings()));
        return Collections.unmodifiableList(all);
    }

    /**
     * All issues regardless of level — errors and warnings combined — from both
     * the object level and every element.  Use this when you need the complete picture.
     *
     * @return the all issues.
     */
    public List<GaiaError> getAllIssues() {
        List<GaiaError> all = new ArrayList<>(errors);
        ais.forEach(e -> all.addAll(e.getIssues()));
        return Collections.unmodifiableList(all);
    }

    /**
     * Returns {@code true} only when there are no object-level errors
     * and no element has any attached errors.
     * {@link GaiaConstants.ErrorLevel#WARNING} advisories do not affect validity.
     *
     * @return {@code true} if this element is valid.
     */
    public boolean isValid() {
        return getErrors().isEmpty()
                && ais.stream().noneMatch(GS1AIObjectElement::hasErrors);
    }

    /**
     * Number of AIs held.
     *
     * @return the size.
     */
    public int size() { return ais.size(); }

    /**
     * Returns the Human Readable Interpretation (HRI) of all AIs.
     * Each AI is rendered as {@code (aiCode)value} and the elements are
     * space-separated, e.g. {@code (01)09506000134352 (17)271231 (10)LOT-ABC}.
     *
     * @return the to HRI string.
     */
    public String toHriString() {
        StringBuilder sb = new StringBuilder();
        for (GS1AIObjectElement e : ais) {
            if (sb.length() > 0) sb.append(' ');
            sb.append('(').append(e.getAi()).append(')').append(e.getValue());
        }
        return sb.toString();
    }

    /**
     * Renders all AIs as a raw GS1 element string — no brackets, each AI code
     * immediately followed by its value, with an FNC1 (GS, ASCII 0x1D) separator
     * after every variable-length element that is not the last
     * ({@link GS1AIObjectElement#isFixedLength()}).
     * Example: {@code 010950600013435210LOT-ABC<GS>17271231}.
     *
     * <p>Only an error-free object can be rendered faithfully, so this method
     * returns {@code null} when {@link #isValid()} is {@code false}.
     *
     * @return the element string, or {@code null} if this object carries errors
     */
    public String toElementString() {
        if (!isValid()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ais.size(); i++) {
            GS1AIObjectElement e = ais.get(i);
            sb.append(e.getAi()).append(e.getValue());
            if (i < ais.size() - 1 && !e.isFixedLength()) {
                sb.append(GS1Constants.FNC1_GS);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the canonical GS1 Digital Link URI for this object
     * (GS1 Digital Link Standard: URI Syntax §4.12), or {@code null} if this
     * object was not parsed from a Digital Link — i.e. no element carries the
     * {@link GS1Constants.DigitalLinkAIType#PRIMARY_IDENTIFICATION_KEY} role.
     *
     * <p>The canonical form uses the {@code https} scheme on the {@code id.gs1.org}
     * domain, the primary identification key and its key qualifiers as path
     * segments (in their canonical order), and data attributes as query
     * parameters <em>sorted lexically by AI key</em>. Reserved characters in
     * values are percent-encoded (§4.2); GTIN values remain 14 digits.
     *
     * <p>Example: an object parsed from
     * {@code http://example.com/01/09520123456788/22/2A?linkType=gs1:traceability}
     * yields {@code https://id.gs1.org/01/09520123456788/22/2A}.
     *
     * @return the canonical GS1 Digital Link URI, or {@code null}
     */
    public String getCanonicalDigitalLink() {
        boolean hasPrimaryKey = ais.stream().anyMatch(e ->
                e.getDigitalLinkAIType() == GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY);
        if (!hasPrimaryKey) return null;

        StringBuilder path = new StringBuilder();
        List<GS1AIObjectElement> dataAttributes = new ArrayList<>();
        for (GS1AIObjectElement e : ais) {
            GS1Constants.DigitalLinkAIType role = e.getDigitalLinkAIType();
            if (role == GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY
                    || role == GS1Constants.DigitalLinkAIType.KEY_QUALIFIER) {
                path.append('/').append(e.getAi()).append('/').append(PcEncUtils.encode(e.getValue()));
            } else if (role == GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE) {
                dataAttributes.add(e);
            }
        }

        // Data attributes become query parameters, sorted lexically by AI key (§4.12).
        dataAttributes.sort(Comparator.comparing(GS1AIObjectElement::getAi));
        StringBuilder query = new StringBuilder();
        for (GS1AIObjectElement e : dataAttributes) {
            query.append(query.length() == 0 ? '?' : '&')
                 .append(e.getAi()).append('=').append(PcEncUtils.encode(e.getValue()));
        }

        return GS1Constants.CANONICAL_DIGITAL_LINK_DOMAIN + path + query;
    }

    @Override
    public String toString() {
        return ais.toString();
    }
}
