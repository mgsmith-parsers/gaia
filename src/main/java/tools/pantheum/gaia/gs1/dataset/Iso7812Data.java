package tools.pantheum.gaia.gs1.dataset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Major Industry Identifier (MII) categories as defined by ISO/IEC 7812-1 — the issuer
 * industry an identification-card number belongs to.
 *
 * <h2>The MII is one digit</h2>
 * <p>ISO/IEC 7812 defines the MII as the <strong>first digit</strong> of the issuer
 * identification number. SIM identifiers conventionally quote the first <em>two</em>
 * digits ({@code 89}) because ITU-T E.118 assigns that pair to telecommunications, so
 * callers holding a 2-digit prefix should use {@link #nameForIdentifier(String)}, which
 * reads the leading digit. {@link #nameForCode(String)} takes the bare digit.
 *
 * <p>For an EID or ICCID the value is therefore always {@code 8} — "healthcare,
 * telecommunications and other future industry assignments" — since every such
 * identifier begins {@code 89}. The category is reported for completeness and
 * traceability to the standard, not as a discriminator.
 *
 * <p>Category names are the ISO/IEC 7812-1 wordings and are held in English only, as
 * with the other reference datasets in this package.
 *
 * @see tools.pantheum.gaia.gs1.interpretation.enricher.EidEnricher
 */
public final class Iso7812Data {

    /** Maps the single MII digit to its ISO/IEC 7812-1 issuer-industry category. */
    public static final Map<String, String> INDUSTRY_CATEGORIES;

    static {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("0", "ISO/TC 68 and other future industry assignments");
        categories.put("1", "Airlines");
        categories.put("2", "Airlines, financial and other future industry assignments");
        categories.put("3", "Travel and entertainment");
        categories.put("4", "Banking and financial");
        categories.put("5", "Banking and financial");
        categories.put("6", "Merchandising and banking/financial");
        categories.put("7", "Petroleum and other future industry assignments");
        categories.put("8", "Healthcare, telecommunications and other future industry assignments");
        categories.put("9", "For assignment by national standards bodies");
        INDUSTRY_CATEGORIES = Collections.unmodifiableMap(categories);
    }

    private Iso7812Data() {}

    /**
     * Returns the industry category for a single MII digit, or {@link Optional#empty()}
     * if the argument is not one of {@code "0"}–{@code "9"}.
     *
     * @param digit one-character string, e.g. {@code "8"} for healthcare/telecommunications
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> nameForCode(String digit) {
        return Optional.ofNullable(INDUSTRY_CATEGORIES.get(digit));
    }

    /**
     * Returns the industry category implied by the leading digit of an issuer identifier
     * prefix, for callers holding more than the MII itself — e.g. the {@code "89"} that
     * an EID or ICCID exposes. Empty for a null or empty argument, or a non-digit lead.
     *
     * @param identifier one or more leading digits of the identifier, e.g. {@code "89"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> nameForIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) return Optional.empty();
        return nameForCode(identifier.substring(0, 1));
    }
}
