package tools.pantheum.gaia.gs1.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents one OR option in an AI's requires list.
 *
 * <p>Three forms:
 * <ul>
 *   <li><b>Single</b> — one exact AI code must be present.</li>
 *   <li><b>Group</b>  — all AI codes in the list must be present (AND).</li>
 *   <li><b>Range</b>  — at least one AI whose code falls within
 *       [{@code rangeStart}, {@code rangeEnd}] must be present.</li>
 * </ul>
 *
 * <p>The outer list of {@code AiRequiresEntry} on an {@link AiDefinition} is OR:
 * at least one entry must be satisfied for the requires constraint to pass.
 */
public class AiRequiresEntry {

    private final String       exactAi;    // non-null for SINGLE
    private final List<String> group;      // non-null for GROUP
    private final String       rangeStart; // non-null for RANGE
    private final String       rangeEnd;   // non-null for RANGE

    /**
     * Of single.
     *
     * @param ai the AI
     * @return a new {@code AiRequiresEntry}
     */
    public static AiRequiresEntry ofSingle(String ai) {
        return new AiRequiresEntry(ai, null, null, null);
    }

    /**
     * Of group.
     *
     * @param ais the AIs
     * @return a new {@code AiRequiresEntry}
     */
    public static AiRequiresEntry ofGroup(List<String> ais) {
        return new AiRequiresEntry(null, Collections.unmodifiableList(new ArrayList<>(ais)), null, null);
    }

    /**
     * Of range.
     *
     * @param start the start
     * @param end the end
     * @return a new {@code AiRequiresEntry}
     */
    public static AiRequiresEntry ofRange(String start, String end) {
        return new AiRequiresEntry(null, null, start, end);
    }

    private AiRequiresEntry(String exactAi, List<String> group, String rangeStart, String rangeEnd) {
        this.exactAi    = exactAi;
        this.group      = group;
        this.rangeStart = rangeStart;
        this.rangeEnd   = rangeEnd;
    }

    /**
     * Returns {@code true} if this element is range.
     *
     * @return {@code true} if this element is range.
     */
    public boolean isRange()  { return rangeStart != null; }
    /**
     * Returns {@code true} if this element is group.
     *
     * @return {@code true} if this element is group.
     */
    public boolean isGroup()  { return group != null; }
    /**
     * Returns {@code true} if this element is single.
     *
     * @return {@code true} if this element is single.
     */
    public boolean isSingle() { return exactAi != null; }

    /**
     * Returns the exact AI.
     *
     * @return the exact AI.
     */
    public String       getExactAi()    { return exactAi; }
    /**
     * Returns the group.
     *
     * @return the group.
     */
    public List<String> getGroup()      { return group; }
    /**
     * Returns the range start.
     *
     * @return the range start.
     */
    public String       getRangeStart() { return rangeStart; }
    /**
     * Returns the range end.
     *
     * @return the range end.
     */
    public String       getRangeEnd()   { return rangeEnd; }

    /**
     * Returns {@code true} if this requires option is satisfied by the given
     * set of present AI codes.
     *
     * <ul>
     *   <li>Single — the exact AI is present.</li>
     *   <li>Group  — all AIs in the group are present.</li>
     *   <li>Range  — at least one present AI falls within the range.</li>
     * </ul>
     *
     * @param presentAis the present AIs
     * @return {@code true} if is satisfied by.
     */
    public boolean isSatisfiedBy(Set<String> presentAis) {
        if (exactAi != null) return presentAis.contains(exactAi);
        if (group != null)   return presentAis.containsAll(group);
        // range: satisfied if any present AI falls within [rangeStart, rangeEnd]
        for (String ai : presentAis) {
            if (ai.compareTo(rangeStart) >= 0 && ai.compareTo(rangeEnd) <= 0) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (exactAi != null) return exactAi;
        if (group != null)   return group.size() == 1 ? group.get(0) : "(" + String.join(" AND ", group) + ")";
        return rangeStart + "-" + rangeEnd;
    }
}
