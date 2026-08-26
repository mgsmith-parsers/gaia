package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.registry.AiComponent;

/**
 * A parsed component slice: the {@link AiComponent} definition paired with the
 * substring of the AI element value that was assigned to it during syntax parsing.
 *
 * <p>Instances are created by {@link tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser}
 * during the component walk (Phase 2) and stored on {@link GS1AIObjectElement} so
 * that content-stage validators can iterate them without re-slicing the raw value.
 */
public final class GS1AIComponentValue {

    private final AiComponent component;
    private final String      value;
    private final int         offset; // zero-based offset within the element's value string

    public GS1AIComponentValue(AiComponent component, String value, int offset) {
        this.component = component;
        this.value     = value;
        this.offset    = offset;
    }

    /** The component definition (type, format, length, flags). */
    public AiComponent getComponent() { return component; }

    /** The substring of the element value assigned to this component. */
    public String getValue()          { return value; }

    /**
     * Zero-based character offset of this slice within the element's full value string.
     * Used to report accurate positions in content-stage errors.
     */
    public int getOffset()            { return offset; }

    // --- Convenience delegates to the underlying AiComponent ---

    /** Component type code: {@code "N"} (numeric) or {@code "X"} (alphanumeric). */
    public String getType()           { return component.getType(); }

    /** Format hint for semantic validation (e.g. {@code "yymmd0"}, {@code "iso3166"}), or {@code null}. */
    public String getFormat()         { return component.getFormat(); }

    /**
     * Human-readable label: the format name if present, otherwise the type code.
     * Used in error messages.
     */
    public String getLabel() {
        String fmt = component.getFormat();
        return (fmt != null && !fmt.isEmpty()) ? fmt : component.getType();
    }
}
