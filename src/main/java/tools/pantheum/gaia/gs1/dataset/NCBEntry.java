package tools.pantheum.gaia.gs1.dataset;

/**
 * Immutable holder for a single National Codification Bureau (NCB) entry.
 *
 * @see NCBData
 */
public final class NCBEntry {

    private final String ncbCode;
    private final String country;
    private final String ctr;
    private final String cat;

    /**
     * Creates a new {@link NCBEntry}.
     *
     * @param ncbCode the NCB code
     * @param country the country
     * @param ctr the ctr
     * @param cat the cat
     */
    public NCBEntry(String ncbCode, String country, String ctr, String cat) {
        this.ncbCode = ncbCode;
        this.country = country;
        this.ctr     = ctr;
        this.cat     = cat;
    }

    /**
     * 2-digit NCB cataloguing nation code, e.g. {@code "66"}.
     *
     * @return the NCB code.
     */
    public String getNcbCode() { return ncbCode; }

    /**
     * Country name in NSPA's official English form, e.g. {@code "Australia"}.
     *
     * @return the country.
     */
    public String getCountry() { return country; }

    /**
     * ISO 3166-1 alpha-3 country code, e.g. {@code "AUS"}; may be {@code null}.
     *
     * @return the ctr.
     */
    public String getCtr()     { return ctr; }

    /**
     * NCS participation category — one of {@code NATO}, {@code TIER1},
     * {@code TIER2}, {@code OTHER} or {@code NSPA}; may be {@code null}.
     *
     * @return the cat.
     */
    public String getCat()     { return cat; }
}
