package tools.pantheum.gaia.gs1.interpretation.registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Describes how to extract one {@link GS1AIInterpretation} from a
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
 *   <li>If {@link #type} is non-null, a base {@link GS1AIInterpretation} is produced
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
     * Simple class name of an {@link InterpretationEnricherInterface} in the
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

    public String  getType()      { return type; }
    public String  getLabel()     { return label; }
    public Integer getComponent() { return component; }
    public Integer getStart()     { return start; }
    public Integer getEnd()       { return end; }
    public String  getEnricher()  { return enricher; }
    public List<String> getTranslatableValueTypes() { return translatableValueTypes; }

    public void setType(String type)           { this.type = type; }
    public void setLabel(String label)         { this.label = label; }
    public void setComponent(Integer component){ this.component = component; }
    public void setStart(Integer start)        { this.start = start; }
    public void setEnd(Integer end)            { this.end = end; }
    public void setEnricher(String enricher)   { this.enricher = enricher; }
    public void setTranslatableValueTypes(List<String> translatableValueTypes) { this.translatableValueTypes = translatableValueTypes; }
}
