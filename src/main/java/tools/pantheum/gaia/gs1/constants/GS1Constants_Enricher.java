package tools.pantheum.gaia.gs1.constants;

/**
 * Interpretation constants consumed by the GS1AIInterpretation enrichers —
 * type keys, their display labels, and enricher-specific values, split out of
 * {@link GS1Constants}.
 *
 * <p>Each key {@code FOO} has a companion {@code FOO_LABEL} holding the
 * human-readable display string used as the second argument to
 * {@link tools.pantheum.gaia.gs1.model.GS1AIInterpretation}.
 */
public final class GS1Constants_Enricher {

    private GS1Constants_Enricher() {}

// =========================================================================
    // Date / time  (DateEnricher, TimeEnricher, DateTimeEnricher,
    //               ProductionTimeEnricher)
    // =========================================================================

    public static final String DATE_VALUE          = "DATE_VALUE";

    public static final String DATE_FORMAT         = "DATE_FORMAT";

    public static final String TIME_VALUE          = "TIME_VALUE";

    public static final String TIME_FORMAT         = "TIME_FORMAT";

    public static final String DATETIME_VALUE      = "DATETIME_VALUE";

    public static final String DATETIME_FORMAT       = "DATETIME_FORMAT";

    // =========================================================================
    // Harvest date  (HarvestDateEnricher)
    // =========================================================================

    public static final String HARVEST_START_DATE       = "HARVEST_START_DATE";

    public static final String HARVEST_END_DATE         = "HARVEST_END_DATE";

    public static final String HARVEST_DATE_RANGE       = "HARVEST_DATE_RANGE";

    // =========================================================================
    // GS1 company prefix  (GS1PrefixEnricher)
    // =========================================================================

    public static final String GS1_COMPANY_PREFIX       = "GS1_COMPANY_PREFIX";

    public static final String GS1_MEMBER_CODE       = "GS1_MEMBER_CODE";

    public static final String GS1_MEMBER_NAME       = "GS1_MEMBER_NAME";

    // =========================================================================
    // GTIN  (GTINEnricher)
    // =========================================================================

    public static final String GTIN_TYPE       = "GTIN_TYPE";

    public static final String GTIN_NATIVE       = "GTIN_NATIVE";

    public static final String PACKAGING_LEVEL       = "PACKAGING_LEVEL";

    public static final String GTIN_CHECK_DIGIT       = "GTIN_CHECK_DIGIT";

    // =========================================================================
    // SSCC  (SSCCEnricher)
    // =========================================================================

    public static final String SSCC_EXTENSION_DIGIT       = "SSCC_EXTENSION_DIGIT";

    public static final String SSCC_SERIAL_REFERENCE       = "SSCC_SERIAL_REFERENCE";

    public static final String SSCC_CHECK_DIGIT       = "SSCC_CHECK_DIGIT";

    // =========================================================================
    // Country / ISO 3166  (Iso3166Enricher, Iso3166Alpha2Enricher,
    //                      Iso3166ListEnricher, NsnEnricher, IBANEnricher)
    // =========================================================================

    public static final String COUNTRY_CODE_NUMERIC       = "COUNTRY_CODE_NUMERIC";

    public static final String COUNTRY_CODE_ALPHA2       = "COUNTRY_CODE_ALPHA2";

    public static final String COUNTRY_NAME       = "COUNTRY_NAME";

    public static final String COUNTRY_LIST       = "COUNTRY_LIST";

    /** Key prefix for per-position numeric country codes in a list, e.g. {@code "COUNTRY_CODE_NUMERIC_1"}. */
    public static final String COUNTRY_CODE_NUMERIC_PREFIX = "COUNTRY_CODE_NUMERIC_";

    /** Key prefix for per-position country names in a list, e.g. {@code "COUNTRY_NAME_1"}. */
    public static final String COUNTRY_NAME_PREFIX = "COUNTRY_NAME_";

    // =========================================================================
    // Currency / ISO 4217  (Iso4217Enricher)
    // =========================================================================

    public static final String CURRENCY_CODE       = "CURRENCY_CODE";

    public static final String CURRENCY_ALPHA       = "CURRENCY_ALPHA";

    public static final String CURRENCY_NAME       = "CURRENCY_NAME";

    // =========================================================================
    // Temperature  (TemperatureEnricherHelper, TemperatureCelsiusEnricher,
    //               TemperatureFahrenheitEnricher)
    // =========================================================================

    public static final String TEMPERATURE       = "TEMPERATURE";

    public static final String TEMPERATURE_UNIT       = "TEMPERATURE_UNIT";

    public static final String TEMPERATURE_FORMATTED       = "TEMPERATURE_FORMATTED";

    /** Celsius unit symbol and display name (AIs 4331, 4333). */
    public static final String TEMPERATURE_UNIT_SYMBOL_C = "°C";
    public static final String TEMPERATURE_UNIT_NAME_C   = "Celsius (°C)";

    /** Fahrenheit unit symbol and display name (AIs 4330, 4332). */
    public static final String TEMPERATURE_UNIT_SYMBOL_F = "°F";
    public static final String TEMPERATURE_UNIT_NAME_F   = "Fahrenheit (°F)";

    // =========================================================================
    // Sex / ISO 5218  (Iso5218Enricher)
    // =========================================================================

    public static final String SEX_CODE       = "SEX_CODE";

    public static final String SEX_DESCRIPTION       = "SEX_DESCRIPTION";

    // =========================================================================
    // Aquatic species / FAO ASFIS  (AquaticSpeciesEnricher)
    // =========================================================================

    public static final String SPECIES_CODE       = "SPECIES_CODE";

    public static final String SPECIES_SCIENTIFIC       = "SPECIES_SCIENTIFIC";

    public static final String SPECIES_ENGLISH       = "SPECIES_ENGLISH";

    public static final String SPECIES_FAMILY       = "SPECIES_FAMILY";

    public static final String SPECIES_ORDER       = "SPECIES_ORDER";

    // =========================================================================
    // NSN / NATO stock number  (NsnEnricher)
    // =========================================================================

    public static final String NSN_FSCG       = "NSN_FSCG";

    public static final String NSN_FSCG_NAME       = "NSN_FSCG_NAME";

    public static final String NSN_FSG       = "NSN_FSG";

    public static final String NSN_FSG_NAME       = "NSN_FSG_NAME";

    public static final String NSN_NCB_COUNTRY_CODE       = "NSN_NCB_COUNTRY_CODE";

    public static final String NSN_NCB_COUNTRY_NAME       = "NSN_NCB_COUNTRY_NAME";

    public static final String NSN_NCB_COUNTRY_CTR       = "NSN_NCB_COUNTRY_CTR";

    public static final String NSN_NCB_COUNTRY_CAT       = "NSN_NCB_COUNTRY_CAT";

    public static final String NSN_NIIN       = "NSN_NIIN";

    public static final String NSN_FORMATTED       = "NSN_FORMATTED";

    // =========================================================================
    // Roll dimensions  (RollDimensionsEnricher)
    // =========================================================================

    public static final String ROLL_WIDTH       = "ROLL_WIDTH";

    public static final String ROLL_LENGTH       = "ROLL_LENGTH";

    public static final String CORE_DIAMETER       = "CORE_DIAMETER";

    public static final String WINDING_DIRECTION_CODE       = "WINDING_DIRECTION_CODE";

    public static final String WINDING_DIRECTION       = "WINDING_DIRECTION";

    public static final String SPLICES       = "SPLICES";

    // =========================================================================
    // IBAN  (IBANEnricher)
    // =========================================================================

    public static final String IBAN_COUNTRY_CODE       = "IBAN_COUNTRY_CODE";

    public static final String IBAN_COUNTRY_NAME       = "IBAN_COUNTRY_NAME";

    public static final String IBAN_CHECK_DIGITS       = "IBAN_CHECK_DIGITS";

    public static final String IBAN_CHECK_VALID       = "IBAN_CHECK_VALID";

    public static final String IBAN_BBAN       = "IBAN_BBAN";

    // =========================================================================
    // Certification reference  (CertificationReferenceEnricher)
    // =========================================================================

    public static final String CERT_SEQUENCE       = "CERT_SEQUENCE";

    public static final String CERT_SCHEME_CODE       = "CERT_SCHEME_CODE";

    public static final String CERT_SCHEME_NAME       = "CERT_SCHEME_NAME";

    public static final String CERT_REFERENCE       = "CERT_REFERENCE";

    // =========================================================================
    // UIC (EU tobacco traceability)  (UicEnricher)
    // =========================================================================

    public static final String UIC_CODE       = "UIC_CODE";

    public static final String UIC_EXTENSION_1       = "UIC_EXTENSION_1";

    public static final String UIC_IMPORTER_INDEX       = "UIC_IMPORTER_INDEX";

    // =========================================================================
    // Birth sequence  (BirthSequenceEnricher)
    // =========================================================================

    public static final String BIRTH_POSITION       = "BIRTH_POSITION";

    public static final String BIRTH_TOTAL       = "BIRTH_TOTAL";

    public static final String BIRTH_SEQUENCE       = "BIRTH_SEQUENCE";

    // =========================================================================
    // GMN — Global Model Number  (GMNEnricher)
    // =========================================================================

    public static final String GMN_MODEL_REFERENCE       = "GMN_MODEL_REFERENCE";

    public static final String GMN_CHECK_PAIR       = "GMN_CHECK_PAIR";

    // =========================================================================
    // HIDRI — Highly Individualised Device Registration Identifier  (HIDRIEnricher)
    // =========================================================================

    public static final String HIDRI_DEVICE_REFERENCE       = "HIDRI_DEVICE_REFERENCE";

    public static final String HIDRI_CHECK_PAIR       = "HIDRI_CHECK_PAIR";

    // =========================================================================
    // CPID — Component/Part Identifier  (CpidEnricher)
    // =========================================================================

    public static final String CPID_PART_REFERENCE       = "CPID_PART_REFERENCE";

    // =========================================================================
    // IMEI — International Mobile Equipment Identity, AIs 8040 / 8041  (ImeiEnricher)
    // =========================================================================

    /**
     * Reporting Body Identifier — the first 2 digits of the TAC, identifying the
     * GSMA-appointed body that allocated it. Carries no validation weight: an
     * unrecognised value may simply be newer than any local allocation table.
     * See {@link #IMEI_RBI_NAME} for the body's name.
     */
    public static final String IMEI_RBI          = "IMEI_RBI";
    /**
     * Name of the reporting body the RBI identifies, when it appears in
     * {@link tools.pantheum.gaia.gs1.dataset.ImeiRbiData}. Omitted for codes absent
     * from that (partial) table — absence carries no validation meaning.
     */
    public static final String IMEI_RBI_NAME     = "IMEI_RBI_NAME";
    /** Type Allocation Code — the first 8 digits of the IMEI. */
    public static final String IMEI_TAC          = "IMEI_TAC";
    /** Serial number — the 6 digits following the TAC. */
    public static final String IMEI_SERIAL       = "IMEI_SERIAL";
    /** Luhn check digit — the final digit of the IMEI. */
    public static final String IMEI_CHECK_DIGIT  = "IMEI_CHECK_DIGIT";
    /** The IMEI in standard GSMA display notation, {@code AA-BBBBBB-CCCCCC-D}. */
    public static final String IMEI_FORMATTED    = "IMEI_FORMATTED";

    // =========================================================================
    // SIM identifiers — AI 8042 ESIM (EID) / AI 8043 PSIM (ICCID)
    //                                        (EidEnricher, IccidEnricher)
    // =========================================================================

    /**
     * Major Industry Identifier — the first two digits of an EID or ICCID. ITU-T E.118
     * assigns the pair {@code 89} to telecommunications; ISO/IEC 7812 defines the MII
     * proper as its leading digit alone, which {@link #SIM_MII_NAME} resolves.
     */
    public static final String SIM_MII         = "SIM_MII";
    /**
     * The ISO/IEC 7812-1 issuer-industry category the MII denotes, resolved from its
     * leading digit via {@link tools.pantheum.gaia.gs1.dataset.Iso7812Data}. For a SIM
     * identifier this is always the category of digit {@code 8}.
     */
    public static final String SIM_MII_NAME    = "SIM_MII_NAME";
    /** The EID digits between the MII and the trailing check digit. */
    public static final String EID_BODY        = "EID_BODY";
    /** Luhn check digit — the 32nd and final digit of the EID. */
    public static final String EID_CHECK_DIGIT = "EID_CHECK_DIGIT";
    /**
     * The ICCID digits following the MII. Not decomposed further: the E.118
     * country-code / issuer-identifier boundary is issuer-dependent and cannot be
     * derived from the value alone.
     */
    public static final String ICCID_BODY      = "ICCID_BODY";
    /**
     * The optional 1–2 digit extension of AI 8043 — its second, optional
     * {@code [N1..N2]} component. Absent when the value is the bare 18 digits.
     */
    public static final String ICCID_EXTENSION = "ICCID_EXTENSION";

    // =========================================================================
    // Decimal / measurement  (DecimalAmountEnricher, DecimalPercentageEnricher,
    //                          DecimalPointEnricher, MonetaryAmountEnricher,
    //                          ISOUnitEnricher)
    // =========================================================================

    public static final String DECIMAL_VALUE       = "DECIMAL_VALUE";

    public static final String DECIMAL_AMOUNT       = "DECIMAL_AMOUNT";

    public static final String DECIMAL_PERCENTAGE       = "DECIMAL_PERCENTAGE";

    /** Number of implied decimal places, taken from the last digit of the AI code. */
    public static final String DECIMAL_PLACES       = "DECIMAL_PLACES";

    public static final String PERCENTAGE_FORMAT       = "PERCENTAGE_FORMAT";

    public static final String ISO_UNIT_CODE       = "ISO_UNIT_CODE";

    public static final String ISO_UNIT_NAME       = "ISO_UNIT_NAME";

    public static final String MONETARY_AMOUNT               = "MONETARY_AMOUNT";

    public static final String MONETARY_AMOUNT_DISPLAY       = "MONETARY_AMOUNT_DISPLAY";

    // =========================================================================
    // Geo coordinates  (GeoCoordinateEnricher)
    // =========================================================================

    public static final String LATITUDE       = "LATITUDE";

    public static final String LONGITUDE       = "LONGITUDE";

    public static final String GEO_COORDINATES       = "GEO_COORDINATES";

    public static final String LATITUDE_DMS       = "LATITUDE_DMS";

    public static final String LONGITUDE_DMS       = "LONGITUDE_DMS";

    public static final String GEO_COORDINATES_DMS       = "GEO_COORDINATES_DMS";

    // =========================================================================
    // Production method  (ProductionMethodEnricher)
    // =========================================================================

    public static final String PRODUCTION_METHOD_CODE       = "PRODUCTION_METHOD_CODE";

    public static final String PRODUCTION_METHOD       = "PRODUCTION_METHOD";

    // =========================================================================
    // AIDC media type  (AIDCMediaTypeEnricher)
    // =========================================================================

    public static final String MEDIA_TYPE_CODE       = "MEDIA_TYPE_CODE";

    public static final String MEDIA_TYPE_NAME       = "MEDIA_TYPE_NAME";

    // =========================================================================
    // Piece of total  (PieceOfTotalEnricher)
    // =========================================================================

    public static final String PIECE_NUMBER       = "PIECE_NUMBER";

    public static final String PIECE_TOTAL       = "PIECE_TOTAL";

    public static final String PIECE_OF_TOTAL       = "PIECE_OF_TOTAL";

    // =========================================================================
    // Miscellaneous
    // =========================================================================

    /** Interpreted boolean flag value — {@code "Yes"} or {@code "No"}.  (YesNoEnricher) */
    public static final String FLAG_VALUE       = "FLAG_VALUE";

    /** Human-readable label for {@link GS1Constants#FLAG_YES_VALUE}. */
    public static final String FLAG_YES_LABEL = "Yes";

    /** Human-readable label for {@link GS1Constants#FLAG_NO_VALUE}. */
    public static final String FLAG_NO_LABEL = "No";

    /** Percent-decoded UTF-8 text.  (PcEncEnricher) */
    public static final String DECODED_TEXT       = "DECODED_TEXT";
}
