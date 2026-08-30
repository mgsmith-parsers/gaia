package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A fully resolved GS1 Application Identifier instance, combining the parsed
 * value with the enriched metadata from the AI definition.
 *
 * <p>Errors discovered during syntax or content validation that are specific to
 * this element can be attached via {@link #addError(GaiaError)} and retrieved
 * via {@link #getErrors()}.
 */
public class GS1AIObjectElement {

    private final String ai;
    private final String title;
    private final String description;
    private final String formatString;
    private final String value;
    private final boolean fixedLength;
    private final int position;
    private final List<GS1AIComponentValue> componentValues;
    private final List<GaiaError>         errors          = new ArrayList<>();
    private final List<GS1AIInterpretation>    interpretations = new ArrayList<>();
    private GS1Constants.DigitalLinkAIType digitalLinkAIType;

    /**
     * Creates a new {@link GS1AIObjectElement}.
     *
     * @param definition the definition
     * @param value the value
     * @param position the position
     * @param componentValues the component values
     */
    public GS1AIObjectElement(AiDefinition definition, String value, int position,
                              List<GS1AIComponentValue> componentValues) {
        this(definition, value, position, componentValues, definition.getDescription());
    }

    /**
     * Creates a new {@link GS1AIObjectElement}.
     *
     * @param description the description to expose, e.g. a localized translation of
     *                     {@link AiDefinition#getDescription()}; the caller resolves it
     *
     * @param definition the definition
     * @param value the value
     * @param position the position
     * @param componentValues the component values
     */
    public GS1AIObjectElement(AiDefinition definition, String value, int position,
                              List<GS1AIComponentValue> componentValues, String description) {
        this.ai              = definition.getApplicationIdentifier();
        this.title           = definition.getTitle();
        this.description     = description;
        this.formatString    = definition.getFormatString();
        this.value           = value;
        this.fixedLength     = definition.isFixedLength();
        this.position        = position;
        this.componentValues = Collections.unmodifiableList(componentValues);
    }

    /**
     * The Application Identifier code (e.g. {@code "01"}, {@code "3102"}).
     *
     * @return the AI.
     */
    public String getAi()           { return ai; }

    /**
     * Short data title from the GS1 spec (e.g. {@code "GTIN"}, {@code "BATCH/LOT"}).
     *
     * @return the title.
     */
    public String getTitle()        { return title; }

    /**
     * Full description of the AI from the GS1 specification, localized to the
     * {@link tools.pantheum.gaia.config.ParseConfig#getLanguage() configured language}
     * via {@link tools.pantheum.gaia.gs1.localization.AiDescriptionRegistry}. An
     * uncatalogued language or AI code keeps the English text from the AI definition.
     *
     * @return the description.
     */
    public String getDescription()  { return description; }

    /**
     * Format descriptor string (e.g. {@code "N14"}, {@code "X..20"}).
     *
     * @return the format string.
     */
    public String getFormatString() { return formatString; }

    /**
     * The raw data value extracted from the element string.
     *
     * @return the value.
     */
    public String getValue()        { return value; }

    /**
     * Whether this AI has a predefined fixed data length.
     *
     * @return {@code true} if this element is fixed length.
     */
    public boolean isFixedLength()  { return fixedLength; }

    /**
     * Zero-based character offset of the AI within the original input string.
     *
     * @return the position.
     */
    public int getPosition()        { return position; }

    /**
     * The role this AI plays in a GS1 Digital Link URI, or {@code null} if the
     * element did not originate from a Digital Link (e.g. a plain element string)
     * or the role has not been determined.
     *
     * @see GS1Constants.DigitalLinkAIType
     *
     * @return the digital link AI type.
     */
    public GS1Constants.DigitalLinkAIType getDigitalLinkAIType() { return digitalLinkAIType; }

    /**
     * Returns {@code true} if a Digital Link AI role has been assigned to this element.
     *
     * @return {@code true} if this element has digital link AI type.
     */
    public boolean hasDigitalLinkAIType() { return digitalLinkAIType != null; }

    /**
     * Assigns the role this AI plays in a GS1 Digital Link URI.
     *
     * @param digitalLinkAIType the role, or {@code null} to clear it
     */
    public void setDigitalLinkAIType(GS1Constants.DigitalLinkAIType digitalLinkAIType) {
        this.digitalLinkAIType = digitalLinkAIType;
    }

    /**
     * The component slices produced during syntax parsing (Phase 2).
     * Each entry pairs the {@link tools.pantheum.gaia.gs1.registry.AiComponent}
     * definition with the substring of {@link #getValue()} assigned to it.
     * For single-component AIs this list has exactly one entry covering the full value.
     *
     * @return the GS1 component values.
     */
    public List<GS1AIComponentValue> getGS1ComponentValues() { return componentValues; }

    /**
     * Attaches a validation error that is specific to this element.
     *
     * @param error the error to attach; must not be {@code null}
     */
    public void addError(GaiaError error) {
        if (error == null) throw new IllegalArgumentException("error must not be null");
        errors.add(error);
    }

    /**
     * All non-WARNING issues attached to this element.
     * Empty when no non-WARNING errors have been attached; warnings are filtered out.
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
     * All {@link GaiaConstants.ErrorLevel#WARNING} advisories attached to this element.
     * Empty when no warnings have been attached.
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
     * All issues attached to this element regardless of level
     * (errors and warnings combined).
     *
     * @return the issues.
     */
    public List<GaiaError> getIssues() { return Collections.unmodifiableList(errors); }

    /**
     * Returns {@code true} if any non-WARNING errors have been attached to this element.
     * Warnings alone do not make an element invalid.
     *
     * @return {@code true} if this element has errors.
     */
    public boolean hasErrors()   { return errors.stream().anyMatch(e -> e.getLevel() != GaiaConstants.ErrorLevel.WARNING); }

    /**
     * Returns {@code true} if any {@link GaiaConstants.ErrorLevel#WARNING} advisories have been attached.
     *
     * @return {@code true} if this element has warnings.
     */
    public boolean hasWarnings() { return errors.stream().anyMatch(e -> e.getLevel() == GaiaConstants.ErrorLevel.WARNING); }

    // -------------------------------------------------------------------------
    // Interpretations (populated during INTERPRETATION parse mode)
    // -------------------------------------------------------------------------

    /**
     * Appends a single {@link GS1AIInterpretation} produced by the interpretation stage.
     *
     * @param interpretation the interpretation to attach; must not be {@code null}
     */
    public void addInterpretation(GS1AIInterpretation interpretation) {
        if (interpretation == null) throw new IllegalArgumentException("interpretation must not be null");
        interpretations.add(interpretation);
    }

    /**
     * Appends all interpretations from the given list (enricher output).
     *
     * @param list interpretations to append; must not be {@code null}
     */
    public void addInterpretations(List<GS1AIInterpretation> list) {
        if (list == null) throw new IllegalArgumentException("list must not be null");
        interpretations.addAll(list);
    }

    /**
     * All interpretations attached to this element.
     * Empty when the interpretation stage has not been run or no definitions exist for this AI.
     *
     * @return the interpretations.
     */
    public List<GS1AIInterpretation> getInterpretations() {
        return Collections.unmodifiableList(interpretations);
    }

    /**
     * Returns the first interpretation whose type matches {@code type}, or {@code null}
     * if no such interpretation exists.
     *
     * <p>Use an interpretation key constant from {@code GS1Constants_Enricher} as the {@code type} argument:
     * <pre>{@code
     * GS1AIInterpretation fmt = el.getInterpretation(GS1Constants_Enricher.GTIN_TYPE);
     * if (fmt != null) System.out.println(fmt.getValue());
     * }</pre>
     *
     * @param type the interpretation type key; must not be {@code null}
     * @return the matching interpretation, or {@code null} if not present
     */
    public GS1AIInterpretation getInterpretation(String type) {
        if (type == null) throw new IllegalArgumentException("type must not be null");
        for (GS1AIInterpretation i : interpretations) {
            if (type.equals(i.getType())) return i;
        }
        return null;
    }

    @Override
    public String toString() {
        return "AI(" + ai + ") [" + title + "] = " + value + " <" + formatString + ">";
    }
}
