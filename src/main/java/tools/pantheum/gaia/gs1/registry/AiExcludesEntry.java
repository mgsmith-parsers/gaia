package tools.pantheum.gaia.gs1.registry;

/**
 * Represents one entry in an AI's excludes list: either an exact AI code or
 * a start–end range.
 *
 * <p>None of the AI codes matched by an {@code AiExcludesEntry} may be present
 * in the same element string as the AI that declares it.
 */
public class AiExcludesEntry {

    private final String exactAi;    // non-null for exact match
    private final String rangeStart; // non-null for range
    private final String rangeEnd;   // non-null for range

    /**
     * Of exact.
     *
     * @param ai the AI
     * @return a new {@code AiExcludesEntry}
     */
    public static AiExcludesEntry ofExact(String ai) {
        return new AiExcludesEntry(ai, null, null);
    }

    /**
     * Of range.
     *
     * @param start the start
     * @param end the end
     * @return a new {@code AiExcludesEntry}
     */
    public static AiExcludesEntry ofRange(String start, String end) {
        return new AiExcludesEntry(null, start, end);
    }

    private AiExcludesEntry(String exactAi, String rangeStart, String rangeEnd) {
        this.exactAi    = exactAi;
        this.rangeStart = rangeStart;
        this.rangeEnd   = rangeEnd;
    }

    /**
     * Returns {@code true} if this element is range.
     *
     * @return {@code true} if this element is range.
     */
    public boolean isRange()   { return rangeStart != null; }
    /**
     * Returns {@code true} if this element is exact.
     *
     * @return {@code true} if this element is exact.
     */
    public boolean isExact()   { return exactAi != null; }

    /**
     * Returns the exact AI.
     *
     * @return the exact AI.
     */
    public String getExactAi()    { return exactAi; }
    /**
     * Returns the range start.
     *
     * @return the range start.
     */
    public String getRangeStart() { return rangeStart; }
    /**
     * Returns the range end.
     *
     * @return the range end.
     */
    public String getRangeEnd()   { return rangeEnd; }

    /**
     * Returns {@code true} if the given AI code is matched by this entry —
     * either an exact match or within the range.
     *
     * @param ai the AI
     * @return {@code true} if matches.
     */
    public boolean matches(String ai) {
        if (exactAi != null) return exactAi.equals(ai);
        return ai.compareTo(rangeStart) >= 0 && ai.compareTo(rangeEnd) <= 0;
    }

    @Override
    public String toString() {
        return exactAi != null ? exactAi : rangeStart + "-" + rangeEnd;
    }
}
