package tools.pantheum.gaia.gs1.registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Describes one data component within a GS1 Application Identifier definition.
 *
 * <p>Each AI's data field may consist of one or more sequentially-ordered components.
 * A component carries:
 * <ul>
 *   <li>{@link #getType()}        — {@code "N"} (numeric) or {@code "X"} (alphanumeric CSET82).</li>
 *   <li>{@link #getLength()}      — maximum number of characters (exact length when {@link #isFixedLength()}).</li>
 *   <li>{@link #isFixedLength()}  — {@code true} if the component always occupies exactly {@code length} characters.</li>
 *   <li>{@link #isOptional()}     — {@code true} if the component may be absent (value exhausted before this component).</li>
 *   <li>{@link #isCheckDigit()}   — {@code true} if the component ends with a standard modulo-10
 *       check digit (GS1 spec §7.9.1).</li>
 *   <li>{@link #isCheckCharacters()} — {@code true} if the component ends with a MOD 1021,32
 *       alphanumeric check character pair (GS1 spec §7.9.5), e.g. GMN (AI 8013) and HIDRI (AI 8014).</li>
 *   <li>{@link #isKey()}          — {@code true} if this component carries the GS1 key portion of the AI value.</li>
 *   <li>{@link #getFormat()}      — optional semantic format hint (e.g. {@code "yymmdd"}, {@code "iso3166"})
 *       used by {@link tools.pantheum.gaia.gs1.content.FormatValidators}.</li>
 * </ul>
 *
 * <p>Instances are deserialised by Jackson from the {@code components} array in
 * {@code gs1-application-identifiers.jsonld}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiComponent {

    /** Creates a new {@link AiComponent}. */
    public AiComponent() {}

    private boolean optional;
    private String type;        // "N" = numeric only, "X" = GS1 AI encodable character set 82
    private boolean fixedLength;
    private int length;
    private boolean checkDigit;
    private boolean checkCharacters;
    private boolean key;
    private String format;      // date format hint, e.g. "yymmd0"

    /**
     * Returns {@code true} if this element is optional.
     *
     * @return {@code true} if this element is optional.
     */
    public boolean isOptional()        { return optional; }
    /**
     * Returns the type.
     *
     * @return the type.
     */
    public String getType()            { return type; }
    /**
     * Returns {@code true} if this element is fixed length.
     *
     * @return {@code true} if this element is fixed length.
     */
    public boolean isFixedLength()     { return fixedLength; }
    /**
     * Returns the length.
     *
     * @return the length.
     */
    public int getLength()             { return length; }
    /**
     * Returns {@code true} if this element is check digit.
     *
     * @return {@code true} if this element is check digit.
     */
    public boolean isCheckDigit()      { return checkDigit; }
    /**
     * Returns {@code true} if this element is check characters.
     *
     * @return {@code true} if this element is check characters.
     */
    public boolean isCheckCharacters() { return checkCharacters; }
    /**
     * Returns {@code true} if this element is key.
     *
     * @return {@code true} if this element is key.
     */
    public boolean isKey()             { return key; }
    /**
     * Returns the format.
     *
     * @return the format.
     */
    public String getFormat()          { return format; }

    /**
     * Set optional.
     *
     * @param optional the optional
     */
    public void setOptional(boolean optional)    { this.optional = optional; }
    /**
     * Set type.
     *
     * @param type the type
     */
    public void setType(String type)             { this.type = type; }
    /**
     * Set fixed length.
     *
     * @param fixedLength the fixed length
     */
    public void setFixedLength(boolean fixedLength) { this.fixedLength = fixedLength; }
    /**
     * Set length.
     *
     * @param length the length
     */
    public void setLength(int length)            { this.length = length; }
    /**
     * Set check digit.
     *
     * @param checkDigit the check digit
     */
    public void setCheckDigit(boolean checkDigit) { this.checkDigit = checkDigit; }
    /**
     * Set check characters.
     *
     * @param checkCharacters the check characters
     */
    public void setCheckCharacters(boolean checkCharacters) { this.checkCharacters = checkCharacters; }
    /**
     * Set key.
     *
     * @param key the key
     */
    public void setKey(boolean key)              { this.key = key; }
    /**
     * Set format.
     *
     * @param format the format
     */
    public void setFormat(String format)         { this.format = format; }
}
