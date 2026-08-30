package tools.pantheum.gaia.gs1.interpretation.registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Describes how to extract one
 * {@link tools.pantheum.gaia.gs1.model.GS1AIInterpretation} from a
 * {@link tools.pantheum.gaia.gs1.model.GS1AIObjectElement}.
 *
 * <p>Instances are loaded from the {@code interpretations} array in
 * {@code ai-content.json} and held by {@link InterpretationRegistry}.
 *
 * <h2>Extraction rules</h2>
 * <ol>
 *   <li>If {@link #component} is non-null, the full value of that component
 *       (by index into {@code element.getGS1ComponentValues()}) is used as
 *       the base value. {@link #start} and {@link #end} are ignored.
 *       If the component index is out of bounds or the component value is
 *       empty (optional component not present), this definition is skipped.</li>
 *   <li>Otherwise, {@link #start} (default 0) and {@link #end} (default
 *       {@code element.getValue().length()}) are applied as substring offsets
 *       on {@code element.getValue()}.</li>
 * </ol>
 *
 * <h2>Output rules</h2>
 * <ul>
 *   <li>If {@link #type} is non-null, a base
 *       {@link tools.pantheum.gaia.gs1.model.GS1AIInterpretation} is produced
 *       with that type, {@link #label}, and the extracted base value.</li>
 *   <li>If {@link #enricher} is non-null, the named enricher is called with
 *       the base value and its results are <em>appended</em> (additive, never
 *       replacing the base interpretation).</li>
 *   <li>Enricher results whose {@code type} appears in {@link #translatableValueTypes}
 *       have their {@code value} looked up in
 *       {@link tools.pantheum.gaia.gs1.localization.ValueRegistry} for the
 *       configured language, in addition to the {@code label} translation every
 *       interpretation already gets. Types not listed keep the value the enricher
 *       emitted, since most enricher values (GTINs, dates, check digits, raw codes) are
 *       extracted data rather than fixed-vocabulary text.</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class InterpretationDefinition {

    /** Creates a new {@link InterpretationDefinition}. */
    public InterpretationDefinition() {}

    /** GS1AIInterpretation type string, e.g. {@code "INDICATOR_DIGIT"}. {@code null} means no base interpretation is produced. */
    private String  type;

    /** Human-readable label for the base interpretation, e.g. {@code "Indicator digit"}. */
    private String  label;

    /**
     * Zero-based index into {@link tools.pantheum.gaia.gs1.model.GS1AIObjectElement#getGS1ComponentValues()}.
     * When non-null, takes priority over {@link #start}/{@link #end}.
     */
    private Integer component;

    /**
     * Inclusive start offset within {@code element.getValue()} (used when {@link #component} is null).
     * Defaults to {@code 0}.
     */
    private Integer start;

    /**
     * Exclusive end offset within {@code element.getValue()} (used when {@link #component} is null).
     * Defaults to {@code element.getValue().length()}.
     */
    private Integer end;

    /**
     * Simple class name of an
     * {@link tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface} in the
     * {@code tools.pantheum.gaia.gs1.interpretation.enricher} package.
     * {@code null} means no enrichment.
     */
    private String  enricher;

    /**
     * Interpretation types (from this definition's enricher output) whose {@code value}
     * should be looked up in {@link tools.pantheum.gaia.gs1.localization.ValueRegistry} for the configured language.
     * {@code null} or empty means none of the enricher's output values are translated.
     */
    private List<String> translatableValueTypes;

    /**
     * Returns the interpretation type string, e.g. {@code "INDICATOR_DIGIT"}.
     *
     * @return the interpretation type string, or {@code null} when no base
     *         interpretation is produced
     */
    public String  getType()      { return type; }
    /**
     * Returns the human-readable label for the base interpretation,
     * e.g. {@code "Indicator digit"}.
     *
     * @return the human-readable label for the base interpretation
     */
    public String  getLabel()     { return label; }
    /**
     * Returns zero-based index into {@link tools.pantheum.gaia.gs1.model.GS1AIObjectElement#getGS1ComponentValues()}.
     *
     * @return zero-based index into {@link tools.pantheum.gaia.gs1.model.GS1AIObjectElement#getGS1ComponentValues()}.
     */
    public Integer getComponent() { return component; }
    /**
     * Returns inclusive start offset within {@code element.getValue()} (used when {@link #component} is null).
     *
     * @return inclusive start offset within {@code element.getValue()} (used when {@link #component} is null).
     */
    public Integer getStart()     { return start; }
    /**
     * Returns exclusive end offset within {@code element.getValue()} (used when {@link #component} is null).
     *
     * @return exclusive end offset within {@code element.getValue()} (used when {@link #component} is null).
     */
    public Integer getEnd()       { return end; }
    /**
     * Returns simple class name of an {@link tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface} in the {@code tools.pantheum.gaia.gs1.interpretation.enricher} package.
     *
     * @return simple class name of an {@link tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface} in the {@code tools.pantheum.gaia.gs1.interpretation.enricher} package.
     */
    public String  getEnricher()  { return enricher; }
    /**
     * Returns interpretation types (from this definition's enricher output) whose {@code value} should be looked up in {@link tools.pantheum.gaia.gs1.localization.ValueRegistry} for the configured language.
     *
     * @return interpretation types (from this definition's enricher output) whose {@code value} should be looked up in {@link tools.pantheum.gaia.gs1.localization.ValueRegistry} for the configured language.
     */
    public List<String> getTranslatableValueTypes() { return translatableValueTypes; }

    /**
     * Set type.
     *
     * @param type the type
     */
    public void setType(String type)           { this.type = type; }
    /**
     * Set label.
     *
     * @param label the label
     */
    public void setLabel(String label)         { this.label = label; }
    /**
     * Set component.
     *
     * @param component the component
     */
    public void setComponent(Integer component){ this.component = component; }
    /**
     * Set start.
     *
     * @param start the start
     */
    public void setStart(Integer start)        { this.start = start; }
    /**
     * Set end.
     *
     * @param end the end
     */
    public void setEnd(Integer end)            { this.end = end; }
    /**
     * Set enricher.
     *
     * @param enricher the enricher
     */
    public void setEnricher(String enricher)   { this.enricher = enricher; }
    /**
     * Set translatable value types.
     *
     * @param translatableValueTypes the translatable value types
     */
    public void setTranslatableValueTypes(List<String> translatableValueTypes) { this.translatableValueTypes = translatableValueTypes; }
}
