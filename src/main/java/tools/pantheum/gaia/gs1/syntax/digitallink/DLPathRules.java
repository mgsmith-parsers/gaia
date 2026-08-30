package tools.pantheum.gaia.gs1.syntax.digitallink;

import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Structural rules of the GS1 Digital Link URI syntax (release 1.7.0).
 *
 * <p>Encodes the normative tables of the <em>GS1 Digital Link Standard: URI
 * Syntax</em>:
 * <ul>
 *   <li><b>§4.3</b> — the AIs usable as <em>primary identification keys</em>
 *       (the first AI/value pair of the URI path), sourced from the
 *       {@code gs1DigitalLinkPrimaryKey} attribute in the AI definitions</li>
 *   <li><b>§4.4 / §4.9</b> — the key qualifiers each primary key admits and the
 *       order in which they must appear, sourced from the
 *       {@code gs1DigitalLinkQualifiers} attribute. Each sequence lists its
 *       qualifier AIs in order; an AI wrapped in square brackets (e.g.
 *       {@code "[10]"}) is optional, an unbracketed AI (e.g. {@code "8020"}) is
 *       required — mirroring the {@code [cpv-comp]} notation of the §4.9 ABNF.</li>
 *   <li><b>§4.10</b> — AIs banned from Digital Link URIs entirely</li>
 * </ul>
 */
public final class DLPathRules {

    private DLPathRules() {}

    /**
     * §4.3 — AIs permitted as the primary identification key of the URI path,
     * read from the {@code gs1DigitalLinkPrimaryKey} flag in the AI definitions.
     */
    private static final Set<String> PRIMARY_KEYS =
            AiDefinitionRegistry.getInstance().all().stream()
                    .filter(AiDefinition::isDigitalLinkPrimaryKey)
                    .map(AiDefinition::getApplicationIdentifier)
                    .collect(Collectors.toUnmodifiableSet());

    /**
     * §4.4 — AIs permitted as key qualifiers in the URI path: the union of all
     * qualifier AIs across every primary key's {@code gs1DigitalLinkQualifiers}
     * sequences (brackets stripped).
     */
    private static final Set<String> KEY_QUALIFIERS =
            AiDefinitionRegistry.getInstance().all().stream()
                    .flatMap(d -> d.getDigitalLinkQualifiers().stream())
                    .flatMap(List::stream)
                    .map(DLPathRules::aiOf)
                    .collect(Collectors.toUnmodifiableSet());

    /**
     * §4.10 — AIs not permitted in a GS1 Digital Link URI at all:
     * (03) MtO GTIN and (8014) HIDRI are restricted to the M-UDI-DI application.
     */
    private static final Set<String> BANNED = Set.of("03", "8014");

    /** The sole option offered by a primary key that admits no qualifiers (key alone). */
    private static final List<DLQualifierOption> KEY_ALONE =
            List.of(DLQualifierOption.of(List.of(), Set.of()));

    /**
     * §4.9 path element order — admissible qualifier sequences per primary key,
     * built once from the {@code gs1DigitalLinkQualifiers} attribute. A primary
     * key with no qualifier sequences maps to {@link #KEY_ALONE} (key alone).
     */
    private static final Map<String, List<DLQualifierOption>> QUALIFIER_OPTIONS =
            AiDefinitionRegistry.getInstance().all().stream()
                    .filter(AiDefinition::isDigitalLinkPrimaryKey)
                    .collect(Collectors.toUnmodifiableMap(
                            AiDefinition::getApplicationIdentifier,
                            DLPathRules::buildOptions));

    /** Builds the §4.9 qualifier options for one primary key from its bracketed sequences. */
    private static List<DLQualifierOption> buildOptions(AiDefinition def) {
        List<List<String>> sequences = def.getDigitalLinkQualifiers();
        if (sequences.isEmpty()) return KEY_ALONE;
        List<DLQualifierOption> options = new ArrayList<>(sequences.size());
        for (List<String> sequence : sequences) {
            List<String> ais = new ArrayList<>(sequence.size());
            Set<String> required = new HashSet<>();
            for (String token : sequence) {
                String ai = aiOf(token);
                ais.add(ai);
                if (!isOptionalToken(token)) required.add(ai);
            }
            options.add(DLQualifierOption.of(ais, required));
        }
        return List.copyOf(options);
    }

    /** The admissible key-qualifier sequences for {@code primaryKey} (§4.9). */
    private static List<DLQualifierOption> optionsFor(String primaryKey) {
        return QUALIFIER_OPTIONS.getOrDefault(primaryKey, KEY_ALONE);
    }

    /** A qualifier token is optional when it is wrapped in square brackets, e.g. {@code "[10]"}. */
    private static boolean isOptionalToken(String token) {
        return token.length() >= 2 && token.charAt(0) == '[' && token.charAt(token.length() - 1) == ']';
    }

    /** Strips the optional-marking square brackets from a qualifier token, leaving the AI code. */
    private static String aiOf(String token) {
        return isOptionalToken(token) ? token.substring(1, token.length() - 1) : token;
    }

    /**
     * Splits a URI path into its non-empty segments, preserving percent-encoding
     * (callers must pass the <em>raw</em>, undecoded path). Single-sourced here so
     * the Digital Link parser and {@code GS1DigitalLinkInfo} agree on exactly what
     * a path segment is when locating the primary key.
     *
     * @param rawPath the raw path
     * @return a new {@code List<String>}
     */
    public static List<String> pathSegments(String rawPath) {
        List<String> segments = new ArrayList<>();
        if (rawPath == null) return segments;
        for (String segment : rawPath.split("/")) {
            if (!segment.isEmpty()) segments.add(segment);
        }
        return segments;
    }

    /**
     * Returns {@code true} if the AI may serve as the primary identification key (§4.3).
     *
     * @param ai the AI
     * @return a new {@code boolean}
     */
    public static boolean isPrimaryKey(String ai) { return PRIMARY_KEYS.contains(ai); }

    /**
     * Returns {@code true} if the AI may serve as a key qualifier (§4.4).
     *
     * @param ai the AI
     * @return a new {@code boolean}
     */
    public static boolean isKeyQualifier(String ai) { return KEY_QUALIFIERS.contains(ai); }

    /**
     * Returns {@code true} if the AI is banned from Digital Link URIs (§4.10).
     *
     * @param ai the AI
     * @return a new {@code boolean}
     */
    public static boolean isBanned(String ai) { return BANNED.contains(ai); }

    /**
     * Validates the qualifier AIs that follow {@code primaryKey} in the URI path
     * against the §4.9 ordering rules.
     *
     * @param primaryKey   the primary identification key AI
     * @param qualifierAis the qualifier AIs in the order they appear in the path
     * @return {@code null} when the sequence is admissible, otherwise a map with a
     *         {@code "code"} entry (the catalogue error id) plus its message data
     *         values (e.g. {@code "ai"} or {@code "primaryKey"})
     */
    /** Catalogue codes for the §4.9 qualifier-ordering failures (Digital Link errors). */
    static final String ERR_CODE_QUALIFIER_IS_PRIMARY_KEY = "GE-L010";
    static final String ERR_CODE_NOT_A_QUALIFIER          = "GE-L011";
    static final String ERR_CODE_QUALIFIER_SEQUENCE       = "GE-L012";

    /**
     * Validate qualifier sequence.
     *
     * @param primaryKey the primary key
     * @param qualifierAis the qualifier AIs
     * @return a new {@code Map<String, String>}
     */
    public static Map<String, String> validateQualifierSequence(String primaryKey, List<String> qualifierAis) {
        for (String ai : qualifierAis) {
            if (!isKeyQualifier(ai)) {
                return isPrimaryKey(ai)
                        ? Map.of("code", ERR_CODE_QUALIFIER_IS_PRIMARY_KEY, "ai", ai)
                        : Map.of("code", ERR_CODE_NOT_A_QUALIFIER, "ai", ai);
            }
        }
        for (DLQualifierOption option : optionsFor(primaryKey)) {
            if (matches(option, qualifierAis)) return null;
        }
        return Map.of("code", ERR_CODE_QUALIFIER_SEQUENCE, "primaryKey", primaryKey);
    }

    /**
     * Orders a set of key-qualifier AIs into the canonical §4.9 sequence for
     * {@code primaryKey}, independent of the order they were supplied in. Unlike
     * {@link #validateQualifierSequence(String, List)} (which validates a path
     * <em>as given</em>, where order matters), this is for <em>constructing</em> a
     * path: the caller has a set of qualifiers and needs them placed in the order
     * the standard requires.
     *
     * @param primaryKey            the primary identification key AI
     * @param suppliedQualifierAis  the qualifier AIs to order, in any order
     * @return the qualifiers in canonical order, or {@code null} if the supplied set
     *         does not satisfy any admissible sequence for the key (an unknown
     *         qualifier, or a required qualifier missing)
     */
    public static List<String> orderQualifiers(String primaryKey, Collection<String> suppliedQualifierAis) {
        Set<String> supplied = new LinkedHashSet<>(suppliedQualifierAis);
        for (DLQualifierOption option : optionsFor(primaryKey)) {
            if (option.ais.containsAll(supplied) && supplied.containsAll(option.required)) {
                List<String> ordered = new ArrayList<>(supplied.size());
                for (String ai : option.ais) {
                    if (supplied.contains(ai)) ordered.add(ai);
                }
                return ordered;
            }
        }
        return null;
    }

    /** Order-preserving subsequence match with required-member check. */
    private static boolean matches(DLQualifierOption option, List<String> qualifierAis) {
        int pos = 0;
        for (String ai : qualifierAis) {
            int found = option.ais.indexOf(ai);
            if (found < 0 || found < pos) return false;
            pos = found + 1;
        }
        for (String required : option.required) {
            if (!qualifierAis.contains(required)) return false;
        }
        return true;
    }
}
