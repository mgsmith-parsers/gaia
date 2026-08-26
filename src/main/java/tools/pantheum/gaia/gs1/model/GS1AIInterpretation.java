package tools.pantheum.gaia.gs1.model;

/**
 * An immutable, human-readable interpretation of a segment of a GS1 AI element.
 *
 * <p>Each interpretation has a machine-readable {@link #type} (e.g.
 * {@code "GS1_COMPANY_PREFIX"}), a human-readable {@link #label}
 * (e.g. {@code "GS1 company prefix"}), and the extracted {@link #value}
 * (e.g. {@code "930"}).
 *
 * <p>Interpretations are produced by
 * {@link tools.pantheum.gaia.gs1.interpretation.InterpretationEngine} during the
 * {@code INTERPRETATION} parse mode and attached to their parent
 * {@link GS1AIObjectElement}.
 */
public final class GS1AIInterpretation {

    private final String type;
    private final String label;
    private final String value;

    public GS1AIInterpretation(String type, String label, String value) {
        this.type  = type;
        this.label = label;
        this.value = value;
    }

    /** Machine-readable interpretation type, e.g. {@code "GS1_COMPANY_PREFIX"}. */
    public String getType()  { return type; }

    /**
     * Human-readable label, localized to the parse language (e.g. {@code "GS1 company prefix"}
     * in English, {@code "Préfixe d'entreprise GS1"} in French). See
     * {@link tools.pantheum.gaia.gs1.localization.LabelRegistry}.
     */
    public String getLabel() { return label; }

    /** Extracted value, e.g. {@code "930"}. */
    public String getValue() { return value; }

    @Override
    public String toString() {
        return type + " [" + label + "]: " + value;
    }
}
