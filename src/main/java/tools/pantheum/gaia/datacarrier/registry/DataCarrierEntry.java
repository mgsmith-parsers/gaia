package tools.pantheum.gaia.datacarrier.registry;

/**
 * Describes a single data carrier variant, identified by its three-character
 * AIM Code ID (e.g. {@code "]C1"} for GS1-128).
 *
 * <p>Instances are loaded from {@code datacarriers.json} at class-initialisation
 * by {@link DataCarrierRegistry}.
 *
 * <p>The field {@code className} corresponds to the {@code "class"} key in the
 * JSON (renamed because {@code class} is a reserved Java keyword).
 */
public final class DataCarrierEntry {

    private final String  aimCodeId;
    private final String  codeChar;
    private final String  modifier;
    private final String  name;
    private final String  description;
    private final String  className;
    private final String  regex;
    private final String  type;
    private final String  standard;
    private final String  note;
    private final boolean eciCapable;
    private final boolean eciActive;
    private final boolean gs1Capable;
    private final boolean gs1AICapable;
    private final boolean gs1DigitalLinkCapable;
    private final boolean requiresGtinPadding;

    /** Derived from {@code aimCodeId}, not supplied by the caller. */
    private final DataCarrierType dataCarrierType;

    /**
     * Creates a new {@link DataCarrierEntry}.
     *
     * @param aimCodeId the AIM code id
     * @param codeChar the code char
     * @param modifier the modifier
     * @param name the name
     * @param description the description
     * @param className the class name
     * @param regex the regex
     * @param type the type
     * @param standard the standard
     * @param note the note
     * @param eciCapable the ECI capable
     * @param eciActive the ECI active
     * @param gs1Capable the GS1 capable
     * @param gs1AICapable the GS1 AI capable
     * @param gs1DigitalLinkCapable the GS1 digital link capable
     * @param requiresGtinPadding the requires GTIN padding
     */
    public DataCarrierEntry(
            String  aimCodeId,
            String  codeChar,
            String  modifier,
            String  name,
            String  description,
            String  className,
            String  regex,
            String  type,
            String  standard,
            String  note,
            boolean eciCapable,
            boolean eciActive,
            boolean gs1Capable,
            boolean gs1AICapable,
            boolean gs1DigitalLinkCapable,
            boolean requiresGtinPadding) {
        this.aimCodeId             = aimCodeId;
        this.codeChar              = codeChar;
        this.modifier              = modifier;
        this.name                  = name;
        this.description           = description;
        this.className             = className;
        this.regex                 = regex;
        this.type                  = type;
        this.standard              = standard;
        this.note                  = note;
        this.eciCapable            = eciCapable;
        this.eciActive             = eciActive;
        this.gs1Capable            = gs1Capable;
        this.gs1AICapable          = gs1AICapable;
        this.gs1DigitalLinkCapable = gs1DigitalLinkCapable;
        this.requiresGtinPadding   = requiresGtinPadding;

        this.dataCarrierType       = DataCarrierType.forAimCodeId(aimCodeId);
    }

    /**
     * AIM Code ID prefix, e.g. {@code "]C1"}.
     *
     * @return the AIM code id.
     */
    public String getAimCodeId() { return aimCodeId; }

    /**
     * Single-character AIM code character, e.g. {@code "C"}.
     *
     * @return the code char.
     */
    public String getCodeChar() { return codeChar; }

    /**
     * Modifier digit as a string, e.g. {@code "1"}.
     *
     * @return the modifier.
     */
    public String getModifier() { return modifier; }

    /**
     * Human-readable symbology name, e.g. {@code "GS1-128 / ISBT 128"}.
     *
     * @return the name.
     */
    public String getName() { return name; }

    /**
     * Description of this specific modifier variant.
     *
     * @return the description.
     */
    public String getDescription() { return description; }

    /**
     * Suggested implementation class name (corresponds to the JSON {@code "class"}
     * field), e.g. {@code "tools.pantheum.gaia.Code128Gs1128Isbt128"}.
     * May be {@code null}.
     *
     * @return the class name.
     */
    public String getClassName() { return className; }

    /**
     * Regular expression that the barcode payload must match for this symbology,
     * e.g. {@code "^[\\x00-\\x7F]+$"}. May be {@code null}.
     *
     * @return the regex.
     */
    public String getRegex() { return regex; }

    /**
     * Raw category string from the JSON: one of {@code "linear"}, {@code "2d"},
     * {@code "stacked_linear"}, {@code "postal"}, {@code "ocr"} or {@code "other"}.
     *
     * <p>For a typed equivalent see {@link #getDataCarrierType()} and
     * {@link DataCarrierType#getCategory()}.
     *
     * @return the type.
     */
    public String getType() { return type; }

    /**
     * The type of data carrier this entry describes, as a constant — e.g.
     * {@link DataCarrierType#QR_CODE}, {@link DataCarrierType#DATA_MATRIX},
     * {@link DataCarrierType#ITF}. Derived from {@link #getAimCodeId()}.
     *
     * @return the resolved type, or {@link DataCarrierType#UNKNOWN} if the AIM
     *         Code ID is absent or unrecognised; never {@code null}
     */
    public DataCarrierType getDataCarrierType() { return dataCarrierType; }

    /**
     * Governing standard, e.g. {@code "ISO/IEC 15417"}.
     * May be {@code null} if not specified.
     *
     * @return the standard.
     */
    public String getStandard() { return standard; }

    /**
     * Optional note. May be {@code null}.
     *
     * @return the note.
     */
    public String getNote() { return note; }

    /**
     * {@code true} if this symbology can carry ECI sequences.
     *
     * @return {@code true} if this element is ECI capable.
     */
    public boolean isEciCapable() { return eciCapable; }

    /**
     * {@code true} if ECI is active (i.e. ECI sequences are present) in this
     * specific variant. Only meaningful when {@link #isEciCapable()} is
     * {@code true}.
     *
     * @return {@code true} if this element is ECI active.
     */
    public boolean isEciActive() { return eciActive; }

    /**
     * {@code true} if this symbology can encode GS1 data.
     *
     * @return {@code true} if this element is GS1 capable.
     */
    public boolean isGs1Capable() { return gs1Capable; }

    /**
     * {@code true} if this symbology can encode a GS1 Application Identifier
     * element string — the key flag used to gate AI parsing.
     *
     * @return {@code true} if this element is GS1 AI capable.
     */
    public boolean isGs1AICapable() { return gs1AICapable; }

    /**
     * {@code true} if this symbology can carry a GS1 Digital Link URI.
     *
     * @return {@code true} if this element is GS1 digital link capable.
     */
    public boolean isGs1DigitalLinkCapable() { return gs1DigitalLinkCapable; }

    /**
     * {@code true} if the raw payload from this symbology must be left-padded
     * with zeros to 14 digits and prefixed with AI {@code (01)} before being
     * parsed as a GS1 element string (applies to EAN-13, UPC-A, UPC-E, EAN-8).
     *
     * @return {@code true} if this element is requires GTIN padding.
     */
    public boolean isRequiresGtinPadding() { return requiresGtinPadding; }

    @Override
    public String toString() {
        return "DataCarrierEntry{aimCodeId='" + aimCodeId + "', name='" + name + "', type='" + type + "'}";
    }
}
