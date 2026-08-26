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

    public static AiExcludesEntry ofExact(String ai) {
        return new AiExcludesEntry(ai, null, null);
    }

    public static AiExcludesEntry ofRange(String start, String end) {
        return new AiExcludesEntry(null, start, end);
    }

    private AiExcludesEntry(String exactAi, String rangeStart, String rangeEnd) {
        this.exactAi    = exactAi;
        this.rangeStart = rangeStart;
        this.rangeEnd   = rangeEnd;
    }

    public boolean isRange()   { return rangeStart != null; }
    public boolean isExact()   { return exactAi != null; }

    public String getExactAi()    { return exactAi; }
    public String getRangeStart() { return rangeStart; }
    public String getRangeEnd()   { return rangeEnd; }

    /**
     * Returns {@code true} if the given AI code is matched by this entry —
     * either an exact match or within the range.
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
