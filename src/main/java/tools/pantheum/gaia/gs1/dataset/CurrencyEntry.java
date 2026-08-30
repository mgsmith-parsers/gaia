package tools.pantheum.gaia.gs1.dataset;

/**
 * All fields for a single ISO 4217 currency entry as found in {@code iso_4217.json}.
 *
 * <p>Instances are created exclusively by {@link Iso4217Data} during class initialisation.
 */
public final class CurrencyEntry {

    private final String numeric;
    private final String code;
    private final String name;
    private final String symbol;
    private final String symbolPosition;
    private final int    decimalPlaces;
    private final String thousandsSeparator;
    private final String decimalCharacter;

    CurrencyEntry(String numeric, String code, String name,
                  String symbol, String symbolPosition,
                  int decimalPlaces, String thousandsSeparator,
                  String decimalCharacter) {
        this.numeric            = numeric;
        this.code               = code;
        this.name               = name;
        this.symbol             = symbol;
        this.symbolPosition     = symbolPosition;
        this.decimalPlaces      = decimalPlaces;
        this.thousandsSeparator = thousandsSeparator;
        this.decimalCharacter   = decimalCharacter;
    }

    /**
     * Zero-padded 3-digit numeric code, e.g. {@code "036"}.
     *
     * @return the numeric.
     */
    public String getNumeric()            { return numeric; }

    /**
     * 3-letter alpha code, e.g. {@code "AUD"}.
     *
     * @return the code.
     */
    public String getCode()               { return code; }

    /**
     * Full currency name, e.g. {@code "Australian Dollar"}.
     *
     * @return the name.
     */
    public String getName()               { return name; }

    /**
     * Currency symbol, e.g. {@code "A$"}.
     *
     * @return the symbol.
     */
    public String getSymbol()             { return symbol; }

    /**
     * Symbol position relative to the amount: {@code "left"} or {@code "right"}.
     *
     * @return the symbol position.
     */
    public String getSymbolPosition()     { return symbolPosition; }

    /**
     * Number of minor-unit decimal places (0, 2, or 3 for most currencies).
     *
     * @return the decimal places.
     */
    public int    getDecimalPlaces()      { return decimalPlaces; }

    /** Conventional thousands separator: {@code "comma"}, {@code "period"},
     * @return the thousands separator.
     *  {@code "space"}, or {@code "apostrophe"}. */
    public String getThousandsSeparator() { return thousandsSeparator; }

    /**
     * Decimal separator character: {@code "period"} or {@code "comma"}.
     *
     * @return the decimal character.
     */
    public String getDecimalCharacter()   { return decimalCharacter; }

    @Override
    public String toString() {
        return code + " (" + numeric + ") — " + name;
    }
}
