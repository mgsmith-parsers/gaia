package tools.pantheum.gaia.gs1.constants;

import java.util.Map;

/**
 * Core constants for the GS1 Application Identifier standard.
 *
 * <p>This class holds the cross-cutting constants — the FNC1 group separator, the
 * canonical GS1 Digital Link domain, the GTIN type-detection parameters used when
 * locating the GS1 company prefix, the certification-scheme and AIDC media-type
 * reference tables shared by validators and enrichers, and the parser's enumerations.
 *
 * <p>Several families of constant live in companion classes to keep this one focused:
 * <ul>
 *   <li>{@link GS1Constants_AICodes} — the numeric Application Identifier code
 *       constants ({@code AI_01_GTIN}, {@code AI_3100_NET_WEIGHT_KG}, …).</li>
 *   <li>{@link GS1Constants_Enricher} — the {@code GS1AIInterpretation} type keys,
 *       their display labels, and enricher-specific values.</li>
 *   <li>{@link GS1Constants_UOM} — UN/ECE Recommendation 20 unit-of-measure codes
 *       for the measurement AIs.</li>
 *   <li>{@link GS1Constants_DateTime} — date and date-time formatting constants.</li>
 * </ul>
 */
public final class GS1Constants {

    private GS1Constants() {}

    /**
     * FNC1 Group Separator — ASCII 0x1D.
     *
     * Used as the separator character between variable-length AI element strings
     * in GS1-128, GS1 DataMatrix, GS1 QR Code, and GS1 DataBar Expanded.
     * Zebra DataWedge preserves this character in the decoded barcode output.
     */
    public static final char FNC1_GS = (char) 0x1D;  // GS (Group Separator / FNC1)

    /**
     * The canonical GS1 Digital Link domain — used as the scheme + authority of a
     * canonical GS1 Digital Link URI (GS1 Digital Link Standard: URI Syntax §4.12).
     */
    public static final String CANONICAL_DIGITAL_LINK_DOMAIN = "https://id.gs1.org";

    // =========================================================================
    // Certification schemes  (CertificationReferenceEnricher,
    //                         CertificationReferenceValidator)
    // =========================================================================

    /**
     * GS1-defined certification scheme codes (component 0 of AIs 7230–7239)
     * mapped to their full human-readable names. The key set is the authoritative
     * list of allowed scheme codes.
     *
     * <ul>
     *   <li>{@code "EM"} — European Marine Equipment Directive (EU Regulation 2018/608)</li>
     * </ul>
     */
    public static final Map<String, String> CERT_SCHEME_NAMES = Map.of(
            "EM", "European Marine Equipment Directive"
    );

    // =========================================================================
    // AIDC media type  (AIDCMediaTypeValidator, AIDCMediaTypeEnricher)
    // =========================================================================

    /**
     * Currently assigned AIDC media type codes (01–10) and their descriptions.
     * Codes 00 and 11–99 are reserved and not valid for use. The key set is the
     * authoritative list of valid codes.
     */
    public static final Map<String, String> AIDC_ASSIGNED_CODES = Map.ofEntries(
            Map.entry("01", "Wristband"),
            Map.entry("02", "Order form"),
            Map.entry("03", "Sample tube"),
            Map.entry("04", "Working list / lab list / form"),
            Map.entry("05", "Test report"),
            Map.entry("06", "Delivery note / issue documentation"),
            Map.entry("07", "Intended recipient label (attached to container)"),
            Map.entry("08", "Label affixed to product"),
            Map.entry("09", "Identity card"),
            Map.entry("10", "Clinical or progress notes")
    );

    // =========================================================================
    // GTIN type detection  (GS1Utils, GTINValidator, ITIPValidator)
    //
    // All GTINs are stored as 14 digits. The number of leading zeros reveals
    // the native format and determines where the GS1 company prefix begins.
    // =========================================================================

    /** GTIN-8 native format identifier. */
    public static final String GTIN_8  = "GTIN-8";

    /** GTIN-12 (UPC-A) native format identifier. */
    public static final String GTIN_12 = "GTIN-12";

    /** GTIN-13 (EAN-13) native format identifier. */
    public static final String GTIN_13 = "GTIN-13";

    /** GTIN-14 native format identifier. */
    public static final String GTIN_14 = "GTIN-14";

    /** Six leading zeros: indicates an embedded GTIN-8. */
    public static final String GTIN_8_PADDING  = "000000";

    /** Two leading zeros: indicates an embedded GTIN-12 (UPC-A). */
    public static final String GTIN_12_PADDING = "00";

    /** One leading zero: indicates an embedded GTIN-13 (EAN-13). */
    public static final String GTIN_13_PADDING = "0";

    /** GTIN-8: strip 6 leading zeros before the prefix check. */
    public static final int GTIN_8_PREFIX_OFFSET  = 6;

    /** GTIN-12: strip 2 leading zeros before the prefix check. */
    public static final int GTIN_12_PREFIX_OFFSET = 2;

    /** GTIN-13 / GTIN-14: strip the leading zero or indicator digit before the prefix check. */
    public static final int GTIN_13_14_PREFIX_OFFSET = 1;

    /** Total length of an SSCC: extension digit + GS1 prefix + serial reference + check digit. */
    public static final int SSCC_LENGTH = 18;

    // =========================================================================
    // Boolean flag values  (YesNoEnricher)
    // =========================================================================

    /** Flag input value {@code "1"} — true/yes. */
    public static final String FLAG_YES_VALUE = "1";

    /** Flag input value {@code "0"} — false/no. */
    public static final String FLAG_NO_VALUE = "0";

    /**
     * The type of GS1 content carried in a barcode or other data carrier.
     *
     * <ul>
     *   <li>{@link #GS1_APPLICATION_IDENTIFIERS} — the payload is a GS1 element string
     *       composed of Application Identifier (AI) / value pairs.</li>
     *   <li>{@link #GS1_DIGITAL_LINK} — the payload is a GS1 Digital Link URI
     *       ({@code http://} or {@code https://}).</li>
     *   <li>{@link #DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL} — the data carrier does not support GS1
     *       Application Identifier element strings or Digital Link URIs (e.g. a non-GS1
     *       symbology such as Code 39).</li>
     *   <li>{@link #UNABLE_TO_DETERMINE_CONTENT} — the content type has not been determined or declared.</li>
     * </ul>
     */
    public enum GS1ContentType {
        /** GS1 element string — one or more AI/value pairs. */
        GS1_APPLICATION_IDENTIFIERS,
        /** GS1 Digital Link URI — AIs encoded as an HTTP(S) URL. */
        GS1_DIGITAL_LINK,
        /** The data carrier supports neither GS1 AI element strings nor Digital Link URIs. */
        DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL,
        /** Content type not determined or declared. */
        UNABLE_TO_DETERMINE_CONTENT
    }

    /** Controls how deeply {@link tools.pantheum.gaia.gs1.GS1AIParser} validates a GS1 element string. */
    public enum ParseMode {
        /** Tokenise and check structure only; skip content validation. */
        SYNTAX,
        /** Full pipeline: syntax tokenisation, structural checks, and content validation. */
        CONTENT,
        /** Full pipeline plus enrichment: decomposes each element into labelled, human-readable interpretation segments. Does not perform additional validation. */
        INTERPRETATION
    }

    /**
     * The role an Application Identifier plays in a GS1 Digital Link URI.
     *
     * <ul>
     *   <li>{@link #PRIMARY_IDENTIFICATION_KEY} — identifies the entity and forms the first path segment,
     *       e.g. {@code /01/09506000134352} (GTIN).</li>
     *   <li>{@link #KEY_QUALIFIER} — refines the primary key in subsequent path segments,
     *       e.g. {@code /10/LOT-A} (batch/lot) or {@code /21/SER123} (serial).</li>
     *   <li>{@link #DATA_ATTRIBUTE} — carries additional data in the query string,
     *       e.g. {@code ?17=271231} (expiry date).</li>
     * </ul>
     */
    public enum DigitalLinkAIType {
        /** Primary identification key — the first path segment of the URI. */
        PRIMARY_IDENTIFICATION_KEY("Primary key"),
        /** Key qualifier — refines the primary key in later path segments. */
        KEY_QUALIFIER("Key qualifier"),
        /** Data attribute — carried as a query-string parameter. */
        DATA_ATTRIBUTE("Data attribute");

        private final String label;

        DigitalLinkAIType(String label) {
            this.label = label;
        }

        /**
         * Human-readable English role label, e.g. {@code "Primary key"}.
         *
         * @return the label.
         */
        public String getLabel() {
            return label;
        }
    }
}
