package tools.pantheum.gaia.correlation;

/**
 * Detects and strips the optional correlation ID prefix from a raw input string.
 *
 * <p>A correlation prefix is exactly 8 ASCII decimal digits followed by a tilde
 * ({@code ~}), e.g. {@code 12345678~}. When present it must appear at the very
 * start of the input, before any GS1 content (AIM Code ID, element string, or
 * Digital Link URI).
 *
 * <h2>Thread safety</h2>
 * {@code CorrelationIdParser} is stateless and thread-safe.
 */
public final class CorrelationIdParser {

    private static final int  CORRELATION_ID_LENGTH = 8;
    private static final char SEPARATOR             = '~';
    private static final int  PREFIX_LENGTH         = CORRELATION_ID_LENGTH + 1; // "DDDDDDDD~"

    /** Creates a new {@link CorrelationIdParser}. */
    public CorrelationIdParser() {}

    /**
     * Inspects {@code input} for a leading {@code DDDDDDDD~} prefix.
     *
     * @param input the raw input string; may be {@code null}
     * @return a {@link CorrelationParseResult}; never {@code null}. When a prefix
     *         was found, {@link CorrelationParseResult#getCorrelationInfo()} is
     *         non-null and {@link CorrelationParseResult#getStrippedPayload()} is
     *         the input with the prefix removed. Otherwise both fields reflect the
     *         no-prefix case: {@code correlationInfo} is {@code null} and
     *         {@code strippedPayload} is the original input.
     */
    public CorrelationParseResult parse(String input) {
        if (input != null
                && input.length() >= PREFIX_LENGTH
                && input.charAt(CORRELATION_ID_LENGTH) == SEPARATOR
                && isAllDigits(input, CORRELATION_ID_LENGTH)) {
            String id      = input.substring(0, CORRELATION_ID_LENGTH);
            String payload = input.substring(PREFIX_LENGTH);
            return new CorrelationParseResult(new CorrelationInfo(id), payload);
        }
        return new CorrelationParseResult(null, input);
    }

    private static boolean isAllDigits(String s, int length) {
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
}
