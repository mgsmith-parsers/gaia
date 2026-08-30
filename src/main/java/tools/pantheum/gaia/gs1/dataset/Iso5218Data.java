package tools.pantheum.gaia.gs1.dataset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reference data for ISO 5218 sex codes.
 *
 * <p>ISO 5218 defines exactly four single-digit codes:
 * <ul>
 *   <li>{@code "0"} — Not known</li>
 *   <li>{@code "1"} — Male</li>
 *   <li>{@code "2"} — Female</li>
 *   <li>{@code "9"} — Not applicable</li>
 * </ul>
 *
 * <p>Use {@link #forCode(String)} to look up the description for a given code.
 * {@link #CODE_TO_NAME} is exposed for direct containment checks.
 */
public final class Iso5218Data {

    /** Maps each valid ISO 5218 code to its description. Insertion-ordered. */
    public static final Map<String, String> CODE_TO_NAME;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "Not known");
        map.put("1", "Male");
        map.put("2", "Female");
        map.put("9", "Not applicable");
        CODE_TO_NAME = Collections.unmodifiableMap(map);
    }

    private Iso5218Data() {}

    /**
     * Returns the description for the given ISO 5218 sex code, or
     * {@link Optional#empty()} if the code is not one of the four defined values.
     *
     * @param code single-digit string: {@code "0"}, {@code "1"}, {@code "2"}, or {@code "9"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> forCode(String code) {
        return Optional.ofNullable(CODE_TO_NAME.get(code));
    }
}
