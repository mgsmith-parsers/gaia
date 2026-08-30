package tools.pantheum.gaia.datacarrier.registry;

import tools.pantheum.gaia.GaiaConstants.DataCarrierTypeCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * The type of data carrier a symbol is — QR Code, Data Matrix, ITF, and so on —
 * as a compile-time constant.
 *
 * <p>Types are identified by the AIM Code ID assigned in ISO/IEC 15424. The
 * character following the {@code ]} (the <em>code character</em>) selects the
 * family; the trailing modifier digit selects a variant within it. Most families
 * therefore map to a single constant covering every modifier — {@link #ITF}
 * covers {@code ]I0} through {@code ]I2}, {@link #EAN_UPC} covers EAN-13, UPC-A,
 * UPC-E and EAN-8 alike.
 *
 * <p>Where GS1 reserves a specific modifier to signal GS1 Application Identifier
 * data, that variant gets its own constant: {@link #GS1_128} ({@code ]C1}),
 * {@link #GS1_DATA_MATRIX} ({@code ]d2}), {@link #GS1_QR_CODE} ({@code ]Q3}) and
 * {@link #GS1_DOT_CODE} ({@code ]J1}) are distinct from their plain counterparts.
 * Those four are the only GS1-reserved variants. {@link #GS1_DATABAR} is not one
 * of them despite its name — every {@code ]e} modifier is GS1 DataBar, so it is a
 * family in its own right with no plain counterpart.
 *
 * <p>Resolve one with {@link DataCarrierEntry#getDataCarrierType()}, or directly
 * from an AIM Code ID via {@link #forAimCodeId(String)}. Use
 * {@link #forAimCodeId(String, boolean)} to collapse those GS1-reserved variants
 * into their plain families.
 */
public enum DataCarrierType {

    // --- Linear ---------------------------------------------------------------

    /** Code 39 — {@code ]A*}. */
    CODE_39(DataCarrierTypeCategory.LINEAR, "A", null, "Code 39"),
    /** Telepen — {@code ]B*}. */
    TELEPEN(DataCarrierTypeCategory.LINEAR, "B", null, "Telepen"),
    /** Code 128, excluding the GS1 variant — {@code ]C0}, {@code ]C2}, {@code ]C4}. */
    CODE_128(DataCarrierTypeCategory.LINEAR, "C", null, "Code 128"),
    /** Channel Code — {@code ]c*}. */
    CHANNEL_CODE(DataCarrierTypeCategory.LINEAR, "c", null, "Channel Code"),
    /** EAN-13, UPC-A, UPC-E and EAN-8 — {@code ]E*}. */
    EAN_UPC(DataCarrierTypeCategory.LINEAR, "E", null, "EAN-13 / UPC-A / UPC-E / EAN-8"),
    /**
     * GS1 DataBar and GS1 Composite — {@code ]e*}.
     *
     * <p>A family, not a GS1-reserved variant: every {@code ]e} modifier is GS1
     * DataBar, so there is no plain counterpart for it to be distinguished from
     * and no reserved AIM Code ID. It is therefore keyed by code character like
     * any other family, and {@link #forAimCodeId(String, boolean)} cannot
     * collapse it.
     */
    GS1_DATABAR(DataCarrierTypeCategory.LINEAR, "e", null, "GS1 DataBar / GS1 Composite"),
    /** Codabar — {@code ]F*}. */
    CODABAR(DataCarrierTypeCategory.LINEAR, "F", null, "Codabar"),
    /** Code 93 / 93i — {@code ]G*}. */
    CODE_93(DataCarrierTypeCategory.LINEAR, "G", null, "Code 93 / 93i"),
    /** Code 11 — {@code ]H*}. */
    CODE_11(DataCarrierTypeCategory.LINEAR, "H", null, "Code 11"),
    /** ITF-14 / Interleaved 2 of 5 — {@code ]I*}. */
    ITF(DataCarrierTypeCategory.LINEAR, "I", null, "ITF-14 (Interleaved 2 of 5)"),
    /** MSI Plessey — {@code ]M*}. */
    MSI_PLESSEY(DataCarrierTypeCategory.LINEAR, "M", null, "MSI Plessey"),
    /** Anker Code — {@code ]N0}. */
    ANKER_CODE(DataCarrierTypeCategory.LINEAR, "N", null, "Anker Code"),
    /** Plessey Code — {@code ]P*}. */
    PLESSEY_CODE(DataCarrierTypeCategory.LINEAR, "P", null, "Plessey Code"),
    /** PosiCode — {@code ]p*}. */
    POSICODE(DataCarrierTypeCategory.LINEAR, "p", null, "PosiCode"),
    /** Straight 2 of 5, two-bar start/stop — {@code ]R*}. */
    STRAIGHT_2_OF_5_TWO_BAR(DataCarrierTypeCategory.LINEAR, "R", null, "Straight 2 of 5 (two-bar start/stop)"),
    /** Straight 2 of 5, three-bar start/stop — {@code ]S*}. */
    STRAIGHT_2_OF_5_THREE_BAR(DataCarrierTypeCategory.LINEAR, "S", null, "Straight 2 of 5 (three-bar start/stop)"),

    // --- Stacked linear -------------------------------------------------------

    /** Code 16K — {@code ]K*}. */
    CODE_16K(DataCarrierTypeCategory.STACKED_LINEAR, "K", null, "Code 16K"),
    /** PDF417 and MicroPDF417 — {@code ]L*}. */
    PDF417(DataCarrierTypeCategory.STACKED_LINEAR, "L", null, "PDF417 / MicroPDF417"),
    /** Codablock — {@code ]O*}. */
    CODABLOCK(DataCarrierTypeCategory.STACKED_LINEAR, "O", null, "Codablock"),
    /** Code 49 — {@code ]T*}. */
    CODE_49(DataCarrierTypeCategory.STACKED_LINEAR, "T", null, "Code 49"),

    // --- Two-dimensional ------------------------------------------------------

    /** Code One — {@code ]D*}. */
    CODE_ONE(DataCarrierTypeCategory.TWO_D, "D", null, "Code One"),
    /** Data Matrix, excluding the GS1 variant — {@code ]d0}, {@code ]d1}, {@code ]d3}–{@code ]d6}. */
    DATA_MATRIX(DataCarrierTypeCategory.TWO_D, "d", null, "Data Matrix"),
    /** Grid Matrix — {@code ]g*}. */
    GRID_MATRIX(DataCarrierTypeCategory.TWO_D, "g", null, "Grid Matrix"),
    /** Han Xin Code — {@code ]h*}. */
    HAN_XIN_CODE(DataCarrierTypeCategory.TWO_D, "h", null, "Han Xin Code"),
    /** DotCode, excluding the GS1 variant — {@code ]J0}. */
    DOT_CODE(DataCarrierTypeCategory.TWO_D, "J", null, "DotCode"),
    /** JAB Code — {@code ]j*}. */
    JAB_CODE(DataCarrierTypeCategory.TWO_D, "j", null, "JAB Code"),
    /** QR Code, excluding the GS1 variant — {@code ]Q0}–{@code ]Q2}, {@code ]Q4}–{@code ]Q6}. */
    QR_CODE(DataCarrierTypeCategory.TWO_D, "Q", null, "QR Code"),
    /** Datastrip 2D — {@code ]r*}. */
    DATASTRIP_2D(DataCarrierTypeCategory.TWO_D, "r", null, "Datastrip 2D"),
    /** SuperCode — {@code ]s*}. */
    SUPERCODE(DataCarrierTypeCategory.TWO_D, "s", null, "SuperCode"),
    /** MaxiCode — {@code ]U*}. */
    MAXICODE(DataCarrierTypeCategory.TWO_D, "U", null, "MaxiCode"),
    /** Ultracode — {@code ]u*}. */
    ULTRACODE(DataCarrierTypeCategory.TWO_D, "u", null, "Ultracode"),
    /** DMRC Code — {@code ]W*}. */
    DMRC_CODE(DataCarrierTypeCategory.TWO_D, "W", null, "DMRC Code"),
    /** Aztec Code — {@code ]z*}. */
    AZTEC_CODE(DataCarrierTypeCategory.TWO_D, "z", null, "Aztec Code"),

    // --- GS1-reserved variants -----------------------------------------------
    // A GS1-reserved modifier of a family that also exists in plain form, so each
    // is keyed by its full AIM Code ID and shadows the plain constant for that ID.
    // GS1 DataBar is deliberately not here — see GS1_DATABAR in the linear section.

    /** GS1-128 / ISBT 128 — {@code ]C1}. */
    GS1_128(DataCarrierTypeCategory.LINEAR, "C", "]C1", "GS1-128 / ISBT 128"),
    /** GS1 DataMatrix — {@code ]d2}. */
    GS1_DATA_MATRIX(DataCarrierTypeCategory.TWO_D, "d", "]d2", "GS1 DataMatrix"),
    /** GS1 DotCode — {@code ]J1}. */
    GS1_DOT_CODE(DataCarrierTypeCategory.TWO_D, "J", "]J1", "GS1 DotCode"),
    /** GS1 QR Code — {@code ]Q3}. */
    GS1_QR_CODE(DataCarrierTypeCategory.TWO_D, "Q", "]Q3", "GS1 QR Code"),
    
    // --- Postal, OCR and catch-alls ------------------------------------------
    
    /** Modulated-height postal codes — {@code ]m*}. */
    MODULATED_HEIGHT_POSTAL(DataCarrierTypeCategory.POSTAL, "m", null, "Modulated Height Postal"),
    /** Optical character recognition — {@code ]o*}. */
    OCR(DataCarrierTypeCategory.OCR, "o", null, "OCR (Optical Character Recognition)"),
    /** A bar code with no other assigned AIM code character — {@code ]X*}. */
    OTHER_BARCODE(DataCarrierTypeCategory.OTHER, "X", null, "Other bar code"),
    /** Non-barcode data — {@code ]Z0}. */
    NON_BARCODE_DATA(DataCarrierTypeCategory.OTHER, "Z", null, "Non-barcode data"),
    
    /** No AIM Code ID was present, or it names a data carrier this registry does not know. */
    UNKNOWN(DataCarrierTypeCategory.OTHER, null, null, "Unknown");
	
    /**
     * Length of an AIM Code ID: {@code ']'} + code character + modifier digit.
     * Mirrors {@code DataCarrierParser.AIM_CODE_ID_LENGTH}; duplicated here so
     * this package does not depend back on the parser package.
     */
    private static final int AIM_CODE_ID_LENGTH = 3;

    /** First character of every AIM Code ID. */
    private static final char AIM_CODE_ID_LEAD = ']';

    /** Only the GS1-reserved variants, keyed by their full AIM Code ID. */
    private static final Map<String, DataCarrierType> GS1_VARIANTS_BY_AIM_CODE_ID = new HashMap<>();

    /** Every non-GS1-reserved family, keyed by AIM code character. */
    private static final Map<String, DataCarrierType> BY_CODE_CHAR = new HashMap<>();

    static {
        for (DataCarrierType t : values()) {
            if (t.gs1AimCodeId != null) {
                GS1_VARIANTS_BY_AIM_CODE_ID.put(t.gs1AimCodeId, t);
            } else if (t.codeChar != null) {
                BY_CODE_CHAR.put(t.codeChar, t);
            }
        }
    }

    private final DataCarrierTypeCategory category;
    private final String   codeChar;
    private final String   gs1AimCodeId;
    private final String   displayName;

    DataCarrierType(DataCarrierTypeCategory category, String codeChar, String gs1AimCodeId, String displayName) {
        this.category     = category;
        this.codeChar     = codeChar;
        this.gs1AimCodeId = gs1AimCodeId;
        this.displayName  = displayName;
    }

    /**
     * Resolves the data carrier type denoted by an AIM Code ID.
     *
     * <p>The argument must be a well-formed AIM Code ID — {@code ']'} followed by
     * an ASCII letter and an ASCII digit — using the same rule as
     * {@code DataCarrierParser.startsWithDataCarrier}. Anything else yields
     * {@link #UNKNOWN} rather than a best guess.
     *
     * <p>An exact match on a GS1-reserved variant wins; otherwise the code
     * character selects the family, so unlisted modifiers still resolve to the
     * right type ({@code "]Q9"} gives {@link #QR_CODE}).
     *
     * @param aimCodeId the three-character AIM Code ID, e.g. {@code "]Q3"};
     *                  may be {@code null}
     * @return the matching constant, or {@link #UNKNOWN} if the ID is absent,
     *         malformed or unrecognised; never {@code null}
     */
    public static DataCarrierType forAimCodeId(String aimCodeId) {
        return forAimCodeId(aimCodeId, false);
    }

    /**
     * Resolves the data carrier type denoted by an AIM Code ID, optionally
     * collapsing the GS1-reserved variants into their plain families.
     *
     * <p>With {@code ignoreGs1Variants} set, the exact-match step is skipped and
     * the code character alone selects the type, so a symbol is reported as the
     * symbology it physically is rather than as its GS1 variant:
     * <pre>
     *   forAimCodeId("]d2", false)  →  GS1_DATA_MATRIX
     *   forAimCodeId("]d2", true)   →  DATA_MATRIX
     *   forAimCodeId("]C1", true)   →  CODE_128
     *   forAimCodeId("]Q3", true)   →  QR_CODE
     *   forAimCodeId("]J1", true)   →  DOT_CODE
     * </pre>
     * Everything else resolves identically either way, since only those four IDs
     * are GS1-reserved. Note {@link #GS1_DATABAR} is a family in its own right
     * rather than a reserved variant — it has no plain counterpart, so
     * {@code "]e0"} still yields {@link #GS1_DATABAR} with the flag set.
     *
     * @param aimCodeId         the three-character AIM Code ID, e.g. {@code "]Q3"};
     *                          may be {@code null}
     * @param ignoreGs1Variants {@code true} to resolve by code character only,
     *                          ignoring the GS1-reserved modifiers
     * @return the matching constant, or {@link #UNKNOWN} if the ID is absent,
     *         malformed or unrecognised; never {@code null}
     */
    public static DataCarrierType forAimCodeId(String aimCodeId, boolean ignoreGs1Variants) {
        if (!isWellFormed(aimCodeId)) {
            return UNKNOWN;
        }
        if (!ignoreGs1Variants) {
            DataCarrierType exact = GS1_VARIANTS_BY_AIM_CODE_ID.get(aimCodeId);
            if (exact != null) {
                return exact;
            }
        }
        DataCarrierType family = BY_CODE_CHAR.get(aimCodeId.substring(1, 2));
        return family != null ? family : UNKNOWN;
    }

    /** Returns {@code true} if {@code aimCodeId} is {@code ']'} + letter + digit. */
    private static boolean isWellFormed(String aimCodeId) {
        return aimCodeId != null
                && aimCodeId.length() >= AIM_CODE_ID_LENGTH
                && aimCodeId.charAt(0) == AIM_CODE_ID_LEAD
                && Character.isLetter(aimCodeId.charAt(1))
                && Character.isDigit(aimCodeId.charAt(2));
    }

    /**
     * The broad structural class this data carrier type belongs to.
     *
     * @return the category.
     */
    public DataCarrierTypeCategory getCategory() { return category; }

    /**
     * The AIM code character identifying this family (the character after
     * {@code ]}), e.g. {@code "Q"} for QR Code. {@code null} for {@link #UNKNOWN}.
     *
     * @return the code char.
     */
    public String getCodeChar() { return codeChar; }

    /**
     * Human-readable name for this type, e.g. {@code "GS1 DataMatrix"}.
     *
     * <p>This names the whole family and so may be broader than the name of the
     * specific variant that resolved to it: {@link DataCarrierEntry#getName()}
     * reports {@code "EAN-8"} for {@code ]E4}, whereas {@link #EAN_UPC} reports
     * {@code "EAN-13 / UPC-A / UPC-E / EAN-8"}. Prefer
     * {@link DataCarrierEntry#getName()} when displaying a scanned symbol, and
     * this when naming the type itself.
     *
     * @return the display name.
     */
    public String getDisplayName() { return displayName; }

    /**
     * {@code true} if this constant always denotes GS1 Application Identifier data.
     *
     * <p>That is the four GS1-reserved variants — {@link #GS1_128},
     * {@link #GS1_DATA_MATRIX}, {@link #GS1_QR_CODE}, {@link #GS1_DOT_CODE} — plus
     * {@link #GS1_DATABAR}, which is inherently GS1 rather than a reserved variant
     * because every {@code ]e} modifier is GS1 DataBar.
     *
     * <p>This asks about the <em>constant</em>, not about a scanned symbol's payload,
     * and is narrower than {@link DataCarrierEntry#isGs1AICapable()}: a plain
     * {@link #QR_CODE} can still carry GS1 AI data, so it is {@code false} here but
     * {@code true} there. Use {@code isGs1AICapable()} to ask whether a carrier is
     * merely capable of GS1 AI data.
     *
     * @return {@code true} if this element is GS1 data carrier.
     */
    public boolean isGs1DataCarrier() { return gs1AimCodeId != null || this == GS1_DATABAR; }
}
