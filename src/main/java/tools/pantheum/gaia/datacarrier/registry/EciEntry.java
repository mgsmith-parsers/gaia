package tools.pantheum.gaia.datacarrier.registry;

/**
 * An Extended Channel Interpretation (ECI) entry, mapping an ECI escape-sequence
 * indicator and numeric ECI number to the character set it designates.
 *
 * <p>Instances are loaded from {@code datacarriers.json} at class-initialisation
 * by {@link DataCarrierRegistry}.
 */
public final class EciEntry {

    private final String indicator;
    private final int    number;
    private final String charset;

    /**
     * Creates a new {@link EciEntry}.
     *
     * @param indicator the indicator
     * @param number the number
     * @param charset the charset
     */
    public EciEntry(String indicator, int number, String charset) {
        this.indicator = indicator;
        this.number    = number;
        this.charset   = charset;
    }

    /**
     * The ECI escape-sequence indicator string (e.g. {@code "\\000026"}).
     *
     * @return the indicator.
     */
    public String getIndicator() { return indicator; }

    /**
     * The ECI protocol number.
     *
     * @return the number.
     */
    public int getNumber() { return number; }

    /**
     * The Java character-set name (or human-readable label) designated by
     * this ECI number (e.g. {@code "UTF-8"}, {@code "ISO-8859-1"},
     * {@code "Reserved"}).
     *
     * @return the charset.
     */
    public String getCharset() { return charset; }

    @Override
    public String toString() {
        return "EciEntry{number=" + number + ", charset='" + charset + "', indicator='" + indicator + "'}";
    }
}
