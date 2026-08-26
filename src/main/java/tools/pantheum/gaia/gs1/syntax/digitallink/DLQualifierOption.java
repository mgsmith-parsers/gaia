package tools.pantheum.gaia.gs1.syntax.digitallink;

import java.util.List;
import java.util.Set;

/**
 * One admissible key-qualifier sequence for a Digital Link primary key
 * (GS1 Digital Link Standard: URI Syntax §4.9).
 *
 * <p>The qualifiers in {@link #ais} may appear only in the given order; those
 * also listed in {@link #required} must be present. Used by {@link DLPathRules}.
 */
final class DLQualifierOption {

    final List<String> ais;
    final Set<String> required;

    private DLQualifierOption(List<String> ais, Set<String> required) {
        this.ais = ais;
        this.required = required;
    }

    /**
     * Builds an option from an ordered qualifier sequence and the subset that
     * must be present. Qualifiers not in {@code required} are optional.
     */
    static DLQualifierOption of(List<String> ais, Set<String> required) {
        return new DLQualifierOption(List.copyOf(ais), Set.copyOf(required));
    }
}
