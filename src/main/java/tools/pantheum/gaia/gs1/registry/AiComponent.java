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

    private boolean optional;
    private String type;        // "N" = numeric only, "X" = GS1 AI encodable character set 82
    private boolean fixedLength;
    private int length;
    private boolean checkDigit;
    private boolean checkCharacters;
    private boolean key;
    private String format;      // date format hint, e.g. "yymmd0"

    public boolean isOptional()        { return optional; }
    public String getType()            { return type; }
    public boolean isFixedLength()     { return fixedLength; }
    public int getLength()             { return length; }
    public boolean isCheckDigit()      { return checkDigit; }
    public boolean isCheckCharacters() { return checkCharacters; }
    public boolean isKey()             { return key; }
    public String getFormat()          { return format; }

    public void setOptional(boolean optional)    { this.optional = optional; }
    public void setType(String type)             { this.type = type; }
    public void setFixedLength(boolean fixedLength) { this.fixedLength = fixedLength; }
    public void setLength(int length)            { this.length = length; }
    public void setCheckDigit(boolean checkDigit) { this.checkDigit = checkDigit; }
    public void setCheckCharacters(boolean checkCharacters) { this.checkCharacters = checkCharacters; }
    public void setKey(boolean key)              { this.key = key; }
    public void setFormat(String format)         { this.format = format; }
}
