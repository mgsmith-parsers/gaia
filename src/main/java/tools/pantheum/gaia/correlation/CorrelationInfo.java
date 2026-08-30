package tools.pantheum.gaia.correlation;

/**
 * Holds the correlation ID extracted from the leading prefix of a GS1 input string.
 *
 * <p>A correlation prefix is an 8-digit numeric identifier followed by a tilde
 * ({@code DDDDDDDD~}), which appears before any GS1 content in the raw input.
 * It is stripped by {@link CorrelationIdParser} before the GS1 parsing pipeline
 * runs and is exposed on the resulting
 * {@link tools.pantheum.gaia.result.ParseResult}.
 */
public final class CorrelationInfo {

    private final String id;

    /**
     * Creates a new {@link CorrelationInfo}.
     *
     * @param id the 8-digit correlation identifier
     */
    public CorrelationInfo(String id) {
        this.id = id;
    }

    /**
     * The 8-digit correlation identifier, e.g. {@code "12345678"}.
     *
     * @return the id.
     */
    public String getId() { return id; }

    @Override
    public String toString() {
        return "CorrelationInfo{id=\"" + id + "\"}";
    }
}
