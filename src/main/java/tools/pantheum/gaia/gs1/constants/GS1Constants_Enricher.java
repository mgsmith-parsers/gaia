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

    /** Interpretation type key {@code DATE_VALUE}. */
    public static final String DATE_VALUE          = "DATE_VALUE";

    /** Interpretation type key {@code DATE_FORMAT}. */
    public static final String DATE_FORMAT         = "DATE_FORMAT";

    /** Interpretation type key {@code TIME_VALUE}. */
    public static final String TIME_VALUE          = "TIME_VALUE";

    /** Interpretation type key {@code TIME_FORMAT}. */
    public static final String TIME_FORMAT         = "TIME_FORMAT";

    /** Interpretation type key {@code DATETIME_VALUE}. */
    public static final String DATETIME_VALUE      = "DATETIME_VALUE";

    /** Interpretation type key {@code DATETIME_FORMAT}. */
    public static final String DATETIME_FORMAT       = "DATETIME_FORMAT";

    // =========================================================================
    // Harvest date  (HarvestDateEnricher)
    // =========================================================================

    /** Interpretation type key {@code HARVEST_START_DATE}. */
    public static final String HARVEST_START_DATE       = "HARVEST_START_DATE";

    /** Interpretation type key {@code HARVEST_END_DATE}. */
    public static final String HARVEST_END_DATE         = "HARVEST_END_DATE";

    /** Interpretation type key {@code HARVEST_DATE_RANGE}. */
    public static final String HARVEST_DATE_RANGE       = "HARVEST_DATE_RANGE";

    // =========================================================================
    // GS1 company prefix  (GS1PrefixEnricher)
    // =========================================================================

    /** Interpretation type key {@code GS1_COMPANY_PREFIX}. */
    public static final String GS1_COMPANY_PREFIX       = "GS1_COMPANY_PREFIX";

    /** Interpretation type key {@code GS1_MEMBER_CODE}. */
    public static final String GS1_MEMBER_CODE       = "GS1_MEMBER_CODE";

    /** Interpretation type key {@code GS1_MEMBER_NAME}. */
    public static final String GS1_MEMBER_NAME       = "GS1_MEMBER_NAME";

    // =========================================================================
    // GTIN  (GTINEnricher)
    // =========================================================================

    /** Interpretation type key {@code GTIN_TYPE}. */
    public static final String GTIN_TYPE       = "GTIN_TYPE";

    /** Interpretation type key {@code GTIN_NATIVE}. */
    public static final String GTIN_NATIVE       = "GTIN_NATIVE";

    /** Interpretation type key {@code PACKAGING_LEVEL}. */
    public static final String PACKAGING_LEVEL       = "PACKAGING_LEVEL";

    /** Interpretation type key {@code GTIN_CHECK_DIGIT}. */
    public static final String GTIN_CHECK_DIGIT       = "GTIN_CHECK_DIGIT";

    // =========================================================================
    // SSCC  (SSCCEnricher)
    // =========================================================================

    /** Interpretation type key {@code SSCC_EXTENSION_DIGIT}. */
    public static final String SSCC_EXTENSION_DIGIT       = "SSCC_EXTENSION_DIGIT";

    /** Interpretation type key {@code SSCC_SERIAL_REFERENCE}. */
    public static final String SSCC_SERIAL_REFERENCE       = "SSCC_SERIAL_REFERENCE";

    /** Interpretation type key {@code SSCC_CHECK_DIGIT}. */
    public static final String SSCC_CHECK_DIGIT       = "SSCC_CHECK_DIGIT";

    // =========================================================================
    // Country / ISO 3166  (Iso3166Enricher, Iso3166Alpha2Enricher,
    //                      Iso3166ListEnricher, NsnEnricher, IBANEnricher)
    // =========================================================================

    /** Interpretation type key {@code COUNTRY_CODE_NUMERIC}. */
    public static final String COUNTRY_CODE_NUMERIC       = "COUNTRY_CODE_NUMERIC";

    /** Interpretation type key {@code COUNTRY_CODE_ALPHA2}. */
    public static final String COUNTRY_CODE_ALPHA2       = "COUNTRY_CODE_ALPHA2";

    /** Interpretation type key {@code COUNTRY_NAME}. */
    public static final String COUNTRY_NAME       = "COUNTRY_NAME";

    /** Interpretation type key {@code COUNTRY_LIST}. */
    public static final String COUNTRY_LIST       = "COUNTRY_LIST";

    /** Key prefix for per-position numeric country codes in a list, e.g. {@code "COUNTRY_CODE_NUMERIC_1"}. */
    public static final String COUNTRY_CODE_NUMERIC_PREFIX = "COUNTRY_CODE_NUMERIC_";

    /** Key prefix for per-position country names in a list, e.g. {@code "COUNTRY_NAME_1"}. */
    public static final String COUNTRY_NAME_PREFIX = "COUNTRY_NAME_";

    // =========================================================================
    // Currency / ISO 4217  (Iso4217Enricher)
    // =========================================================================

    /** Interpretation type key {@code CURRENCY_CODE}. */
    public static final String CURRENCY_CODE       = "CURRENCY_CODE";

    /** Interpretation type key {@code CURRENCY_ALPHA}. */
    public static final String CURRENCY_ALPHA       = "CURRENCY_ALPHA";

    /** Interpretation type key {@code CURRENCY_NAME}. */
    public static final String CURRENCY_NAME       = "CURRENCY_NAME";

    // =========================================================================
    // Temperature  (TemperatureEnricherHelper, TemperatureCelsiusEnricher,
    //               TemperatureFahrenheitEnricher)
    // =========================================================================

    /** Interpretation type key {@code TEMPERATURE}. */
    public static final String TEMPERATURE       = "TEMPERATURE";

    /** Interpretation type key {@code TEMPERATURE_UNIT}. */
    public static final String TEMPERATURE_UNIT       = "TEMPERATURE_UNIT";

    /** Interpretation type key {@code TEMPERATURE_FORMATTED}. */
    public static final String TEMPERATURE_FORMATTED       = "TEMPERATURE_FORMATTED";

    /** Celsius unit symbol and display name (AIs 4331, 4333). */
    public static final String TEMPERATURE_UNIT_SYMBOL_C = "°C";
    /** Interpretation type key {@code TEMPERATURE_UNIT_NAME_C}. */
    public static final String TEMPERATURE_UNIT_NAME_C   = "Celsius (°C)";

    /** Fahrenheit unit symbol and display name (AIs 4330, 4332). */
    public static final String TEMPERATURE_UNIT_SYMBOL_F = "°F";
    /** Interpretation type key {@code TEMPERATURE_UNIT_NAME_F}. */
    public static final String TEMPERATURE_UNIT_NAME_F   = "Fahrenheit (°F)";

    // =========================================================================
    // Sex / ISO 5218  (Iso5218Enricher)
    // =========================================================================

    /** Interpretation type key {@code SEX_CODE}. */
    public static final String SEX_CODE       = "SEX_CODE";

    /** Interpretation type key {@code SEX_DESCRIPTION}. */
    public static final String SEX_DESCRIPTION       = "SEX_DESCRIPTION";

    // =========================================================================
    // Aquatic species / FAO ASFIS  (AquaticSpeciesEnricher)
    // =========================================================================

    /** Interpretation type key {@code SPECIES_CODE}. */
    public static final String SPECIES_CODE       = "SPECIES_CODE";

    /** Interpretation type key {@code SPECIES_SCIENTIFIC}. */
    public static final String SPECIES_SCIENTIFIC       = "SPECIES_SCIENTIFIC";

    /** Interpretation type key {@code SPECIES_ENGLISH}. */
    public static final String SPECIES_ENGLISH       = "SPECIES_ENGLISH";

    /** Interpretation type key {@code SPECIES_FAMILY}. */
    public static final String SPECIES_FAMILY       = "SPECIES_FAMILY";

    /** Interpretation type key {@code SPECIES_ORDER}. */
    public static final String SPECIES_ORDER       = "SPECIES_ORDER";

    // =========================================================================
    // NSN / NATO stock number  (NsnEnricher)
    // =========================================================================

    /** Interpretation type key {@code NSN_FSCG}. */
    public static final String NSN_FSCG       = "NSN_FSCG";

    /** Interpretation type key {@code NSN_FSCG_NAME}. */
    public static final String NSN_FSCG_NAME       = "NSN_FSCG_NAME";

    /** Interpretation type key {@code NSN_FSG}. */
    public static final String NSN_FSG       = "NSN_FSG";

    /** Interpretation type key {@code NSN_FSG_NAME}. */
    public static final String NSN_FSG_NAME       = "NSN_FSG_NAME";

    /** Interpretation type key {@code NSN_NCB_COUNTRY_CODE}. */
    public static final String NSN_NCB_COUNTRY_CODE       = "NSN_NCB_COUNTRY_CODE";

    /** Interpretation type key {@code NSN_NCB_COUNTRY_NAME}. */
    public static final String NSN_NCB_COUNTRY_NAME       = "NSN_NCB_COUNTRY_NAME";

    /** Interpretation type key {@code NSN_NCB_COUNTRY_CTR}. */
    public static final String NSN_NCB_COUNTRY_CTR       = "NSN_NCB_COUNTRY_CTR";

    /** Interpretation type key {@code NSN_NCB_COUNTRY_CAT}. */
    public static final String NSN_NCB_COUNTRY_CAT       = "NSN_NCB_COUNTRY_CAT";

    /** Interpretation type key {@code NSN_NIIN}. */
    public static final String NSN_NIIN       = "NSN_NIIN";

    /** Interpretation type key {@code NSN_FORMATTED}. */
    public static final String NSN_FORMATTED       = "NSN_FORMATTED";

    // =========================================================================
    // Roll dimensions  (RollDimensionsEnricher)
    // =========================================================================

    /** Interpretation type key {@code ROLL_WIDTH}. */
    public static final String ROLL_WIDTH       = "ROLL_WIDTH";

    /** Interpretation type key {@code ROLL_LENGTH}. */
    public static final String ROLL_LENGTH       = "ROLL_LENGTH";

    /** Interpretation type key {@code CORE_DIAMETER}. */
    public static final String CORE_DIAMETER       = "CORE_DIAMETER";

    /** Interpretation type key {@code WINDING_DIRECTION_CODE}. */
    public static final String WINDING_DIRECTION_CODE       = "WINDING_DIRECTION_CODE";

    /** Interpretation type key {@code WINDING_DIRECTION}. */
    public static final String WINDING_DIRECTION       = "WINDING_DIRECTION";

    /** Interpretation type key {@code SPLICES}. */
    public static final String SPLICES       = "SPLICES";

    // =========================================================================
    // IBAN  (IBANEnricher)
    // =========================================================================

    /** Interpretation type key {@code IBAN_COUNTRY_CODE}. */
    public static final String IBAN_COUNTRY_CODE       = "IBAN_COUNTRY_CODE";

    /** Interpretation type key {@code IBAN_COUNTRY_NAME}. */
    public static final String IBAN_COUNTRY_NAME       = "IBAN_COUNTRY_NAME";

    /** Interpretation type key {@code IBAN_CHECK_DIGITS}. */
    public static final String IBAN_CHECK_DIGITS       = "IBAN_CHECK_DIGITS";

    /** Interpretation type key {@code IBAN_CHECK_VALID}. */
    public static final String IBAN_CHECK_VALID       = "IBAN_CHECK_VALID";

    /** Interpretation type key {@code IBAN_BBAN}. */
    public static final String IBAN_BBAN       = "IBAN_BBAN";

    // =========================================================================
    // Certification reference  (CertificationReferenceEnricher)
    // =========================================================================

    /** Interpretation type key {@code CERT_SEQUENCE}. */
    public static final String CERT_SEQUENCE       = "CERT_SEQUENCE";

    /** Interpretation type key {@code CERT_SCHEME_CODE}. */
    public static final String CERT_SCHEME_CODE       = "CERT_SCHEME_CODE";

    /** Interpretation type key {@code CERT_SCHEME_NAME}. */
    public static final String CERT_SCHEME_NAME       = "CERT_SCHEME_NAME";

    /** Interpretation type key {@code CERT_REFERENCE}. */
    public static final String CERT_REFERENCE       = "CERT_REFERENCE";

    // =========================================================================
    // UIC (EU tobacco traceability)  (UicEnricher)
    // =========================================================================

    /** Interpretation type key {@code UIC_CODE}. */
    public static final String UIC_CODE       = "UIC_CODE";

    /** Interpretation type key {@code UIC_EXTENSION_1}. */
    public static final String UIC_EXTENSION_1       = "UIC_EXTENSION_1";

    /** Interpretation type key {@code UIC_IMPORTER_INDEX}. */
    public static final String UIC_IMPORTER_INDEX       = "UIC_IMPORTER_INDEX";

    // =========================================================================
    // Birth sequence  (BirthSequenceEnricher)
    // =========================================================================

    /** Interpretation type key {@code BIRTH_POSITION}. */
    public static final String BIRTH_POSITION       = "BIRTH_POSITION";

    /** Interpretation type key {@code BIRTH_TOTAL}. */
    public static final String BIRTH_TOTAL       = "BIRTH_TOTAL";

    /** Interpretation type key {@code BIRTH_SEQUENCE}. */
    public static final String BIRTH_SEQUENCE       = "BIRTH_SEQUENCE";

    // =========================================================================
    // GMN — Global Model Number  (GMNEnricher)
    // =========================================================================

    /** Interpretation type key {@code GMN_MODEL_REFERENCE}. */
    public static final String GMN_MODEL_REFERENCE       = "GMN_MODEL_REFERENCE";

    /** Interpretation type key {@code GMN_CHECK_PAIR}. */
    public static final String GMN_CHECK_PAIR       = "GMN_CHECK_PAIR";

    // =========================================================================
    // HIDRI — Highly Individualised Device Registration Identifier  (HIDRIEnricher)
    // =========================================================================

    /** Interpretation type key {@code HIDRI_DEVICE_REFERENCE}. */
    public static final String HIDRI_DEVICE_REFERENCE       = "HIDRI_DEVICE_REFERENCE";

    /** Interpretation type key {@code HIDRI_CHECK_PAIR}. */
    public static final String HIDRI_CHECK_PAIR       = "HIDRI_CHECK_PAIR";

    // =========================================================================
    // CPID — Component/Part Identifier  (CpidEnricher)
    // =========================================================================

    /** Interpretation type key {@code CPID_PART_REFERENCE}. */
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

    /** Interpretation type key {@code DECIMAL_VALUE}. */
    public static final String DECIMAL_VALUE       = "DECIMAL_VALUE";

    /** Interpretation type key {@code DECIMAL_AMOUNT}. */
    public static final String DECIMAL_AMOUNT       = "DECIMAL_AMOUNT";

    /** Interpretation type key {@code DECIMAL_PERCENTAGE}. */
    public static final String DECIMAL_PERCENTAGE       = "DECIMAL_PERCENTAGE";

    /** Number of implied decimal places, taken from the last digit of the AI code. */
    public static final String DECIMAL_PLACES       = "DECIMAL_PLACES";

    /** Interpretation type key {@code PERCENTAGE_FORMAT}. */
    public static final String PERCENTAGE_FORMAT       = "PERCENTAGE_FORMAT";

    /** Interpretation type key {@code ISO_UNIT_CODE}. */
    public static final String ISO_UNIT_CODE       = "ISO_UNIT_CODE";

    /** Interpretation type key {@code ISO_UNIT_NAME}. */
    public static final String ISO_UNIT_NAME       = "ISO_UNIT_NAME";

    /** Interpretation type key {@code MONETARY_AMOUNT}. */
    public static final String MONETARY_AMOUNT               = "MONETARY_AMOUNT";

    /** Interpretation type key {@code MONETARY_AMOUNT_DISPLAY}. */
    public static final String MONETARY_AMOUNT_DISPLAY       = "MONETARY_AMOUNT_DISPLAY";

    // =========================================================================
    // Geo coordinates  (GeoCoordinateEnricher)
    // =========================================================================

    /** Interpretation type key {@code LATITUDE}. */
    public static final String LATITUDE       = "LATITUDE";

    /** Interpretation type key {@code LONGITUDE}. */
    public static final String LONGITUDE       = "LONGITUDE";

    /** Interpretation type key {@code GEO_COORDINATES}. */
    public static final String GEO_COORDINATES       = "GEO_COORDINATES";

    /** Interpretation type key {@code LATITUDE_DMS}. */
    public static final String LATITUDE_DMS       = "LATITUDE_DMS";

    /** Interpretation type key {@code LONGITUDE_DMS}. */
    public static final String LONGITUDE_DMS       = "LONGITUDE_DMS";

    /** Interpretation type key {@code GEO_COORDINATES_DMS}. */
    public static final String GEO_COORDINATES_DMS       = "GEO_COORDINATES_DMS";

    // =========================================================================
    // Production method  (ProductionMethodEnricher)
    // =========================================================================

    /** Interpretation type key {@code PRODUCTION_METHOD_CODE}. */
    public static final String PRODUCTION_METHOD_CODE       = "PRODUCTION_METHOD_CODE";

    /** Interpretation type key {@code PRODUCTION_METHOD}. */
    public static final String PRODUCTION_METHOD       = "PRODUCTION_METHOD";

    // =========================================================================
    // AIDC media type  (AIDCMediaTypeEnricher)
    // =========================================================================

    /** Interpretation type key {@code MEDIA_TYPE_CODE}. */
    public static final String MEDIA_TYPE_CODE       = "MEDIA_TYPE_CODE";

    /** Interpretation type key {@code MEDIA_TYPE_NAME}. */
    public static final String MEDIA_TYPE_NAME       = "MEDIA_TYPE_NAME";

    // =========================================================================
    // Piece of total  (PieceOfTotalEnricher)
    // =========================================================================

    /** Interpretation type key {@code PIECE_NUMBER}. */
    public static final String PIECE_NUMBER       = "PIECE_NUMBER";

    /** Interpretation type key {@code PIECE_TOTAL}. */
    public static final String PIECE_TOTAL       = "PIECE_TOTAL";

    /** Interpretation type key {@code PIECE_OF_TOTAL}. */
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
