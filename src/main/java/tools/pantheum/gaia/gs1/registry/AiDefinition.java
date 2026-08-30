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

    /**
     * Creates a new {@link AiDefinition}.
     *
     * @param applicationIdentifier the application identifier
     * @param formatString the format string
     * @param description the description
     * @param title the title
     * @param regex the regex
     * @param separatorRequired the separator required
     * @param validAsDataAttribute the valid as data attribute
     * @param digitalLinkPrimaryKey the digital link primary key
     * @param digitalLinkQualifiers the digital link qualifiers
     * @param components the components
     * @param requires the requires
     * @param excludes the excludes
     */
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

    /**
     * Returns the application identifier.
     *
     * @return the application identifier.
     */
    public String              getApplicationIdentifier() { return applicationIdentifier; }
    /**
     * Returns the format string.
     *
     * @return the format string.
     */
    public String              getFormatString()          { return formatString; }
    /**
     * Returns the description.
     *
     * @return the description.
     */
    public String              getDescription()           { return description; }
    /**
     * Returns the title.
     *
     * @return the title.
     */
    public String              getTitle()                 { return title; }
    /**
     * Returns the regex.
     *
     * @return the regex.
     */
    public String              getRegex()                 { return regex; }
    /**
     * Returns {@code true} if this element is separator required.
     *
     * @return {@code true} if this element is separator required.
     */
    public boolean             isSeparatorRequired()      { return separatorRequired; }

    /**
     * Whether this AI may be carried as a data attribute — e.g. in the query
     * string of a GS1 Digital Link URI (GS1 Digital Link: URI Syntax §4.10).
     * {@code false} for AIs that are only valid as primary keys or key qualifiers
     * (serial number, CPV, GLN extension, etc.).
     *
     * @return {@code true} if this element is valid as data attribute.
     */
    public boolean             isValidAsDataAttribute()   { return validAsDataAttribute; }

    /**
     * Whether this AI may serve as the primary identification key of a GS1
     * Digital Link URI (GS1 Digital Link: URI Syntax §4.3) — the first
     * {@code /ai/value} pair of the URI path. Sourced from the
     * {@code gs1DigitalLinkPrimaryKey} attribute in the AI definitions.
     *
     * @return {@code true} if this element is digital link primary key.
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
     *
     * @return the digital link qualifiers.
     */
    public List<List<String>>  getDigitalLinkQualifiers() { return digitalLinkQualifiers; }
    /**
     * Returns the components.
     *
     * @return the components.
     */
    public List<AiComponent>   getComponents()            { return components; }
    /**
     * Returns the component count.
     *
     * @return the component count.
     */
    public int                 getComponentCount()        { return components.size(); }
    /**
     * Returns the requires.
     *
     * @return the requires.
     */
    public List<AiRequiresEntry> getRequires()              { return requires; }
    /**
     * Returns the excludes.
     *
     * @return the excludes.
     */
    public List<AiExcludesEntry> getExcludes()              { return excludes; }

    /**
     * Total number of data characters for fixed-length AIs; 0 for variable-length.
     *
     * @return the fixed data length.
     */
    public int     getFixedDataLength() { return fixedDataLength; }
    /**
     * Returns {@code true} if this element is fixed length.
     *
     * @return {@code true} if this element is fixed length.
     */
    public boolean isFixedLength()      { return fixedDataLength > 0; }
    /**
     * Returns the compiled regex.
     *
     * @return the compiled regex.
     */
    public Pattern getCompiledRegex()   { return compiledRegex; }

}
