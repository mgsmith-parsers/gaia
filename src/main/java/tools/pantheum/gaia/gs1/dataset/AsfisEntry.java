package tools.pantheum.gaia.gs1.dataset;

/**
 * Immutable holder for a single FAO ASFIS species entry.
 *
 * @see AsfisData
 */
public final class AsfisEntry {

    private final String alpha3Code;
    private final String scientificName;
    private final String englishName;   // may be null
    private final String family;        // may be null
    private final String order;         // may be null

    public AsfisEntry(String alpha3Code, String scientificName,
                      String englishName, String family, String order) {
        this.alpha3Code     = alpha3Code;
        this.scientificName = scientificName;
        this.englishName    = englishName;
        this.family         = family;
        this.order          = order;
    }

    /** 3-letter FAO Alpha-3 species code, e.g. {@code "COD"}. */
    public String getAlpha3Code()     { return alpha3Code; }

    /** Scientific (Latin) name, e.g. {@code "Gadus morhua"}. */
    public String getScientificName() { return scientificName; }

    /** English common name, e.g. {@code "Atlantic cod"}; may be {@code null}. */
    public String getEnglishName()    { return englishName; }

    /** Family name in uppercase, e.g. {@code "GADIDAE"}; may be {@code null}. */
    public String getFamily()         { return family; }

    /** Order or higher taxon in uppercase, e.g. {@code "GADIFORMES"}; may be {@code null}. */
    public String getOrder()          { return order; }
}
