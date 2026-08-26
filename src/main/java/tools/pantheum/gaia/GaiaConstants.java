package tools.pantheum.gaia;

/**
 * Top-level constants for the Gaia GS1 parsing library.
 */
public final class GaiaConstants {

    private GaiaConstants() {}

    /** Classpath location of the data-carrier (AIM Code ID / symbology) registry. */
    public static final String DATA_CARRIERS_RESOURCE = "/datacarriers.json";

    /**
     * Severity level of a {@link tools.pantheum.gaia.error.GaiaError}.
     *
     * <p>Only {@link #WARNING} is non-blocking — it does not cause {@code isValid()}
     * to return {@code false} and does not stop the parsing pipeline.
     *
     * <ul>
     *   <li>{@link #SYNTAX_ERROR}    — tokenisation failure; parsing cannot continue past this point.</li>
     *   <li>{@link #FORMAT_ERROR}    — value fails regex or component format rules.</li>
     *   <li>{@link #INTEGRITY_ERROR} — structural violation (duplicate AI, missing dependency, excluded pairing).</li>
     *   <li>{@link #DATA_ERROR}      — value fails check digit or semantic custom validation.</li>
     *   <li>{@link #WARNING}         — advisory notice; pipeline continues and result is still valid.</li>
     * </ul>
     */
    public enum ErrorLevel {
        SYNTAX_ERROR,
        FORMAT_ERROR,
        INTEGRITY_ERROR,
        DATA_ERROR,
        /** Advisory notice — does not affect {@code isValid()} or stop the pipeline. */
        WARNING
    }

    /**
     * Pipeline stage in which a {@link tools.pantheum.gaia.error.GaiaError} was produced.
     *
     * <ul>
     *   <li>{@link #DATA_CARRIER} — AIM Code ID / symbology detection.</li>
     *   <li>{@link #SYNTAX}       — tokenisation and structural integrity (GE-S* / GE-D* errors).</li>
     *   <li>{@link #CONTENT}      — format, check digit, and custom semantic validation (GE-C* errors).</li>
     *   <li>{@link #INTERNAL}     — unexpected runtime errors caught by the top-level handler (GE-I*).</li>
     * </ul>
     */
    public enum ErrorStage {
        DATA_CARRIER,
        DIGITAL_LINK,
        SYNTAX,
        CONTENT,
        INTERNAL
    }

    /** Controls how deeply the parser validates a GS1 element string. */
    public enum ParseMode {
        /** Tokenise and check structure only; skip content validation. */
        SYNTAX,
        /** Full pipeline: syntax tokenisation, structural checks, and content validation. */
        CONTENT,
        /** Validate the data carrier encoding (symbology-level checks). */
        DATA_CARRIER,
        /** Validate the human-readable interpretation of the element string. */
        INTERPRETATION
    }

    /**
     * The language used for error messages and other human-readable output produced by the parser.
     *
     * <ul>
     *   <li>{@link #ENGLISH} — messages are written in English (default)</li>
     *   <li>{@link #FRENCH}  — messages are written in French</li>
     * </ul>
     *
     * @see tools.pantheum.gaia.config.ParseConfig
     */
    public enum Language {
        /** English (default). */
        ENGLISH,
        /** French — Français. */
        FRENCH,
        /** Spanish — Español. */
        SPANISH,
        /** German — Deutsch. */
        GERMAN,
        /** Italian — Italiano. */
        ITALIAN,
        /** Portuguese — Português. */
        PORTUGUESE,
        /** Dutch — Nederlands. */
        DUTCH,
        /** Polish — Polski. */
        POLISH,
        /** Russian — Русский. */
        RUSSIAN,
        /** Ukrainian — Українська. */
        UKRAINIAN,
        /** Czech — Čeština. */
        CZECH,
        /** Swedish — Svenska. */
        SWEDISH,
        /** Chinese (Simplified) — 简体中文. */
        CHINESE,
        /** Japanese — 日本語. */
        JAPANESE,
        /** Korean — 한국어. */
        KOREAN,
        /** Arabic — العربية. */
        ARABIC,
        /** Indonesian — Bahasa Indonesia. */
        INDONESIAN,
        /** Hindi — हिन्दी. */
        HINDI,
        /** Turkish — Türkçe. */
        TURKISH,
        /** Bengali — বাংলা. */
        BENGALI,
        /** Urdu — اردو. */
        URDU,
        /** Vietnamese — Tiếng Việt. */
        VIETNAMESE,
        /** Nigerian Pidgin — Naijá. */
        NIGERIAN_PIDGIN,
        /** Egyptian Arabic — العربية المصرية. */
        EGYPTIAN_ARABIC,
        /** Marathi — मराठी. */
        MARATHI,
        /** Telugu — తెలుగు. */
        TELUGU,
        /** Tamil — தமிழ். */
        TAMIL,
        /** Cantonese (Yue) — 粵語. */
        CANTONESE,
        /** Wu Chinese — 吳語. */
        WU_CHINESE,
        /** Tagalog (Filipino) — Tagalog. */
        TAGALOG,
        /** Persian (Farsi) — فارسی. */
        PERSIAN,
        /** Hausa — Hausa. */
        HAUSA,
        /** Punjabi — ਪੰਜਾਬੀ. */
        PUNJABI,
        /** Javanese — Basa Jawa. */
        JAVANESE,
        /** Swahili — Kiswahili. */
        SWAHILI
    }

    /**
     * The separator character placed between day, month, and year in a formatted date string.
     *
     * <ul>
     *   <li>{@link #SLASH}  — {@code "/"}, e.g. {@code 31/12/2026} (default)</li>
     *   <li>{@link #HYPHEN} — {@code "-"}, e.g. {@code 2026-12-31} (ISO 8601)</li>
     *   <li>{@link #PERIOD} — {@code "."}, e.g. {@code 31.12.2026} (European)</li>
     * </ul>
     *
     * @see tools.pantheum.gaia.config.ParseConfig
     */
    public enum DateSeparator {
        /** Forward slash: {@code /} */
        SLASH("/"),
        /** Hyphen / minus sign: {@code -} */
        HYPHEN("-"),
        /** Full stop / period: {@code .} */
        PERIOD(".");

        private final String symbol;

        DateSeparator(String symbol) { this.symbol = symbol; }

        /** Returns the single-character string used between date components, e.g. {@code "/"}. */
        public String symbol() { return symbol; }
    }

    /**
     * Controls the order in which day, month, and year are written in a formatted date string.
     *
     * <ul>
     *   <li>{@link #LITTLE} — day-month-year, e.g. {@code 31/12/2026}</li>
     *   <li>{@link #MIDDLE} — month-day-year, e.g. {@code 12/31/2026}</li>
     *   <li>{@link #BIG}    — year-month-day, e.g. {@code 2026/12/31}</li>
     * </ul>
     *
     * @see tools.pantheum.gaia.config.ParseConfig
     */
    public enum DateEndian {
        /** Day-Month-Year (little-endian), e.g. {@code 31/12/2026}. Most of the world. */
        LITTLE,
        /** Month-Day-Year (middle-endian), e.g. {@code 12/31/2026}. United States. */
        MIDDLE,
        /** Year-Month-Day (big-endian), e.g. {@code 2026/12/31}. ISO 8601 / East Asia. */
        BIG
    }

    /**
     * Controls how the month is written in a formatted date string.
     *
     * <ul>
     *   <li>{@link #TWO_DIGIT}    — zero-padded number, e.g. {@code 31/12/2026} (default)</li>
     *   <li>{@link #THREE_LETTER} — three-letter English abbreviation, e.g. {@code 31/DEC/2026}</li>
     * </ul>
     *
     * @see tools.pantheum.gaia.config.ParseConfig
     */
    public enum MonthFormat {
        /** Two-digit zero-padded month, e.g. {@code 12}. */
        TWO_DIGIT,
        /** Three-letter English month abbreviation, e.g. {@code DEC}. */
        THREE_LETTER
    }

    /**
     * Controls how the year is written in a formatted date string.
     *
     * <ul>
     *   <li>{@link #FOUR_DIGIT} — full four-digit year, e.g. {@code 31/12/2026} (default)</li>
     *   <li>{@link #TWO_DIGIT}  — two-digit year, e.g. {@code 31/12/26}</li>
     * </ul>
     *
     * @see tools.pantheum.gaia.config.ParseConfig
     */
    public enum YearFormat {
        /** Full four-digit year, e.g. {@code 2026}. */
        FOUR_DIGIT,
        /** Two-digit year, e.g. {@code 26}. */
        TWO_DIGIT
    }

    /**
     * The broad structural class of a data carrier, mirroring the {@code type}
     * field of {@code datacarriers.json}.
     *
     * @see tools.pantheum.gaia.datacarrier.registry.DataCarrierType#getCategory()
     */
    public enum DataCarrierTypeCategory {
        /** A one-dimensional bar code. */
        LINEAR,
        /** Multiple linear rows stacked vertically. */
        STACKED_LINEAR,
        /** A two-dimensional matrix or dot symbol. */
        TWO_D,
        /** A postal bar code. */
        POSTAL,
        /** Optical character recognition rather than a bar code. */
        OCR,
        /** Anything else, including non-barcode data. */
        OTHER
    }
}
