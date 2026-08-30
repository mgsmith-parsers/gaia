package tools.pantheum.gaia.correlation;

/**
 * The result of a {@link CorrelationIdParser#parse(String)} call.
 *
 * <p>Carries the optional {@link CorrelationInfo} extracted from the input and
 * the stripped payload that should be forwarded to the GS1 parsing pipeline.
 * When no correlation prefix was detected, {@link #getCorrelationInfo()} returns
 * {@code null} and {@link #getStrippedPayload()} returns the original input unchanged.
 */
public final class CorrelationParseResult {

    private final CorrelationInfo correlationInfo;
    private final String          strippedPayload;

    CorrelationParseResult(CorrelationInfo correlationInfo, String strippedPayload) {
        this.correlationInfo = correlationInfo;
        this.strippedPayload = strippedPayload;
    }

    /**
     * The extracted correlation identifier, or {@code null} if the input did not
     * start with a valid {@code DDDDDDDD~} prefix.
     *
     * @return the correlation info.
     */
    public CorrelationInfo getCorrelationInfo() { return correlationInfo; }

    /**
     * The input with the {@code DDDDDDDD~} prefix removed, or the original input
     * if no prefix was detected.
     *
     * @return the stripped payload.
     */
    public String getStrippedPayload() { return strippedPayload; }

    /**
     * Returns {@code true} if a correlation prefix was found and stripped.
     *
     * @return {@code true} if this element has correlation id.
     */
    public boolean hasCorrelationId() { return correlationInfo != null; }
}
