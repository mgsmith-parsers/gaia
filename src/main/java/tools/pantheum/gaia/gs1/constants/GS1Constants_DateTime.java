package tools.pantheum.gaia.gs1.constants;

import java.util.Set;

/**
 * Date and date-time formatting constants, split out of
 * {@link tools.pantheum.gaia.gs1.interpretation.enricher.DateEnricher} and
 * {@link tools.pantheum.gaia.gs1.interpretation.enricher.DateTimeEnricher}.
 *
 * <p>Covers the GS1 date component formats, raw value lengths, calendar bounds,
 * zero-padding specifiers, human-readable pattern tokens, month abbreviations,
 * and the date-time combination separators.
 */
public final class GS1Constants_DateTime {

    private GS1Constants_DateTime() {}

    // --- GS1 date component formats (the AI definition's "format" field) ---
    public static final String FORMAT_YYMMD0   = "yymmd0";
    public static final String FORMAT_YYMMDD   = "yymmdd";
    public static final String FORMAT_YYYYMMDD = "yyyymmdd";
    public static final Set<String> DATE_FORMATS = Set.of(FORMAT_YYMMD0, FORMAT_YYMMDD, FORMAT_YYYYMMDD);

    // --- Raw value lengths ---
    public static final int SHORT_DATE_LENGTH = 6;  // yymmdd / yymmd0
    public static final int FULL_DATE_LENGTH  = 8;  // yyyymmdd

    /** A {@code DD=00} day in a {@code yymmd0} value denotes the last day of the month. */
    public static final String END_OF_MONTH_DAY = "00";

    // --- Calendar bounds ---
    public static final int FIRST_MONTH       = 1;
    public static final int LAST_MONTH        = 12;
    public static final int YEARS_PER_CENTURY = 100;

    // --- Zero-padding format specifiers ---
    public static final String PAD_TWO_DIGITS  = "%02d";
    public static final String PAD_FOUR_DIGITS = "%04d";

    // --- Human-readable date pattern tokens ---
    public static final String TOKEN_DAY        = "dd";
    public static final String TOKEN_MONTH      = "mm";
    public static final String TOKEN_MONTH_ABBR = "mmm";
    public static final String TOKEN_YEAR_TWO   = "yy";
    public static final String TOKEN_YEAR_FOUR  = "yyyy";

    /** Three-letter English month abbreviations (uppercase), indexed by month number minus one. */
    public static final String[] MONTH_ABBREVIATIONS = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    // --- Date-time combination (DateTimeEnricher) ---

    /** Separator placed between the date and time portions of the combined value and format. */
    public static final String DATE_TIME_SEPARATOR = " ";

    /** Time format used when the element carries no {@code TIME_FORMAT} interpretation. */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm";

    /**
     * Number of interpretations a date or date-time enricher produces
     * (a {@code *_VALUE} plus a {@code *_FORMAT}).
     */
    public static final int INTERPRETATION_COUNT = 2;
}
