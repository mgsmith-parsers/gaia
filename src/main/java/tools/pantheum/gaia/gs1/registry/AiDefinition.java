package tools.pantheum.gaia.gs1.registry;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Parsed representation of one entry in gs1-application-identifiers.jsonld.
 *
 * <p>Requires semantics (outer = OR, inner = AND):
 * Each {@link AiRequiresEntry} is one option (OR). All AIs in a single
 * {@code AiRequiresEntry} must be present (AND); at least one entry must be
 * satisfied.
 *
 * <p>Excludes semantics: each {@link AiExcludesEntry} covers either an exact AI
 * code or a start–end range. None may be present together with this AI.
 */
public class AiDefinition {

    private final String applicationIdentifier;
    private final String formatString;
    private final String description;
    private final String title;
    private final String regex;
    private final boolean separatorRequired;
    private final boolean validAsDataAttribute;
    private final boolean digitalLinkPrimaryKey;
    private final List<List<String>> digitalLinkQualifiers;
    private final List<AiComponent> components;
    private final List<AiRequiresEntry> requires;
    private final List<AiExcludesEntry> excludes;
    private final int fixedDataLength;  // 0 if variable length
    private final Pattern compiledRegex;  // null if no regex defined

    public AiDefinition(String applicationIdentifier,
                        String formatString,
                        String description,
                        String title,
                        String regex,
                        boolean separatorRequired,
                        boolean validAsDataAttribute,
                        boolean digitalLinkPrimaryKey,
                        List<List<String>> digitalLinkQualifiers,
                        List<AiComponent> components,
                        List<AiRequiresEntry> requires,
                        List<AiExcludesEntry> excludes) {
        this.applicationIdentifier = applicationIdentifier;
        this.formatString          = formatString;
        this.description           = description;
        this.title                 = title;
        this.regex                 = regex;
        this.separatorRequired     = separatorRequired;
        this.validAsDataAttribute  = validAsDataAttribute;
        this.digitalLinkPrimaryKey = digitalLinkPrimaryKey;
        this.digitalLinkQualifiers = deepUnmodifiable(digitalLinkQualifiers);
        this.components            = Collections.unmodifiableList(components);
        this.requires              = Collections.unmodifiableList(requires);
        this.excludes              = Collections.unmodifiableList(excludes);
        this.fixedDataLength       = computeFixedDataLength(components, separatorRequired);
        this.compiledRegex         = compileRegex(regex);
    }

    private static List<List<String>> deepUnmodifiable(List<List<String>> lists) {
        List<List<String>> copy = new java.util.ArrayList<>(lists.size());
        for (List<String> inner : lists) copy.add(Collections.unmodifiableList(inner));
        return Collections.unmodifiableList(copy);
    }

    private static Pattern compileRegex(String regex) {
        if (regex == null || regex.isEmpty()) return null;
        try {
            return Pattern.compile("^" + regex + "$");
        } catch (PatternSyntaxException e) {
            throw new IllegalStateException(
                    "Malformed regex in AI definition '" + regex + "': " + e.getMessage(), e);
        }
    }

    private static int computeFixedDataLength(List<AiComponent> components, boolean separatorRequired) {
        if (separatorRequired) return 0;
        int total = 0;
        for (AiComponent c : components) {
            if (!c.isFixedLength()) return 0;
            total += c.getLength();
        }
        return total;
    }

    public String              getApplicationIdentifier() { return applicationIdentifier; }
    public String              getFormatString()          { return formatString; }
    public String              getDescription()           { return description; }
    public String              getTitle()                 { return title; }
    public String              getRegex()                 { return regex; }
    public boolean             isSeparatorRequired()      { return separatorRequired; }

    /**
     * Whether this AI may be carried as a data attribute — e.g. in the query
     * string of a GS1 Digital Link URI (GS1 Digital Link: URI Syntax §4.10).
     * {@code false} for AIs that are only valid as primary keys or key qualifiers
     * (serial number, CPV, GLN extension, etc.).
     */
    public boolean             isValidAsDataAttribute()   { return validAsDataAttribute; }

    /**
     * Whether this AI may serve as the primary identification key of a GS1
     * Digital Link URI (GS1 Digital Link: URI Syntax §4.3) — the first
     * {@code /ai/value} pair of the URI path. Sourced from the
     * {@code gs1DigitalLinkPrimaryKey} attribute in the AI definitions.
     */
    public boolean             isDigitalLinkPrimaryKey()  { return digitalLinkPrimaryKey; }

    /**
     * The admissible GS1 Digital Link key-qualifier sequences for this primary
     * key (GS1 Digital Link: URI Syntax §4.4/§4.9), sourced from the
     * {@code gs1DigitalLinkQualifiers} attribute. Each inner list is one ordered
     * sequence of qualifier AIs; an AI wrapped in square brackets (e.g.
     * {@code "[10]"}) is optional, an unbracketed AI (e.g. {@code "8020"}) is
     * required — mirroring the {@code [cpv-comp]} notation of the §4.9 ABNF.
     * Example for GTIN: {@code [["[22]","[10]","[21]"], ["235"]]}.
     * Empty for AIs that are not primary keys or admit no qualifiers.
     */
    public List<List<String>>  getDigitalLinkQualifiers() { return digitalLinkQualifiers; }
    public List<AiComponent>   getComponents()            { return components; }
    public int                 getComponentCount()        { return components.size(); }
    public List<AiRequiresEntry> getRequires()              { return requires; }
    public List<AiExcludesEntry> getExcludes()              { return excludes; }

    /** Total number of data characters for fixed-length AIs; 0 for variable-length. */
    public int     getFixedDataLength() { return fixedDataLength; }
    public boolean isFixedLength()      { return fixedDataLength > 0; }
    public Pattern getCompiledRegex()   { return compiledRegex; }

}
