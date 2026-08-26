package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants_DateTime;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.util.DateUtils;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiComponent;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a GS1 date component value with a human-readable formatted date string.
 *
 * <p>Handles all three GS1 date formats:
 * <ul>
 *   <li>{@code yymmd0} (6 digits) — two-digit year, month, and day where
 *       {@code DD=00} denotes the last day of the month (end-of-month
 *       convention). The actual last day is computed from the year and month.</li>
 *   <li>{@code yymmdd} (6 digits) — two-digit year, month, explicit day.
 *       Year is interpreted as {@code 20YY}.</li>
 *   <li>{@code yyyymmdd} (8 digits) — four-digit year, month, explicit day.</li>
 * </ul>
 *
 * <p>The GS1 date format is detected automatically:
 * <ol>
 *   <li>If the value is 8 characters → {@code yyyymmdd}.</li>
 *   <li>If the value is 6 characters → the AI definition's components are
 *       scanned for a component whose {@code format} field is {@code "yymmd0"}
 *       or {@code "yymmdd"}; the first match wins. Defaults to {@code "yymmdd"}
 *       if no match is found.</li>
 * </ol>
 *
 * <h2>Produced interpretations</h2>
 * <p>The output format is controlled by the {@link tools.pantheum.gaia.config.ParseConfig}
 * passed to the pipeline — its {@link tools.pantheum.gaia.config.ParseConfig#getDateEndian()},
 * {@link tools.pantheum.gaia.config.ParseConfig#getDateSeparator()},
 * {@link tools.pantheum.gaia.config.ParseConfig#getMonthFormat()} (two-digit or
 * three-letter month), and {@link tools.pantheum.gaia.config.ParseConfig#getYearFormat()}
 * (four-digit or two-digit year).
 * <ul>
 *   <li>{@code DATE_VALUE}  — formatted date string whose order, separator, month
 *       and year representations are determined by {@code ParseConfig}, e.g.
 *       {@code "15/06/2025"} (default: little-endian, slash, two-digit month,
 *       four-digit year) or {@code "15/JUN/25"} (three-letter month, two-digit
 *       year). For {@code yymmd0} with {@code DD=00} the actual last day of the
 *       month is computed, e.g. {@code "31/01/2025"} for January end-of-month.</li>
 *   <li>{@code DATE_FORMAT} — the human-readable pattern string corresponding to
 *       the active config, e.g. {@code "dd/mm/yyyy"} (little-endian, slash),
 *       {@code "yyyy-mm-dd"} (big-endian, hyphen), {@code "dd/mmm/yy"}
 *       (three-letter month, two-digit year).</li>
 * </ul>
 */
public final class DateEnricher implements InterpretationEnricherInterface {

    public DateEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        return enrich(baseValue, aiDefinition, element, ParseConfig.defaultConfig());
    }

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                            GS1AIObjectElement element, ParseConfig config) {
        if (baseValue == null || baseValue.length() < GS1Constants_DateTime.SHORT_DATE_LENGTH) return Collections.emptyList();

        String dateFormat = detectFormat(baseValue, aiDefinition);
        String formatted  = format(baseValue, dateFormat, config);
        if (formatted == null) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(GS1Constants_DateTime.INTERPRETATION_COUNT);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATE_VALUE,  null,  formatted));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.DATE_FORMAT, null, datePattern(config)));
        return results;
    }

    // -------------------------------------------------------------------------

    /**
     * Detects the date format from the value length and the AI's component
     * definitions.
     */
    private static String detectFormat(String value, AiDefinition aiDefinition) {
        if (value.length() == GS1Constants_DateTime.FULL_DATE_LENGTH) return GS1Constants_DateTime.FORMAT_YYYYMMDD;

        // 6-char: scan AI components for a date format hint
        if (aiDefinition != null) {
            for (AiComponent comp : aiDefinition.getComponents()) {
                String fmt = comp.getFormat();
                if (GS1Constants_DateTime.DATE_FORMATS.contains(fmt)) return fmt;
            }
        }
        return GS1Constants_DateTime.FORMAT_YYMMDD; // default
    }

    /**
     * Formats a raw GS1 date string using the given {@link ParseConfig}.
     * Returns {@code null} if the value cannot be parsed.
     */
    static String format(String raw, String dateFormat, ParseConfig config) {
        try {
            String sep = config.getDateSeparator().symbol();
            int    year, month;
            String dd;

            if (GS1Constants_DateTime.FORMAT_YYYYMMDD.equals(dateFormat)) {
                year  = Integer.parseInt(raw.substring(0, 4));
                month = Integer.parseInt(raw.substring(4, 6));
                dd    = raw.substring(6, 8);
            } else {
                // yymmd0 or yymmdd — 6-digit value YYMMDD
                year  = DateUtils.resolveYear(Integer.parseInt(raw.substring(0, 2)));
                month = Integer.parseInt(raw.substring(2, 4));
                dd    = raw.substring(4, 6);

                if (GS1Constants_DateTime.END_OF_MONTH_DAY.equals(dd) && GS1Constants_DateTime.FORMAT_YYMMD0.equals(dateFormat)) {
                    // DD=00 means last day of the month — compute it
                    int lastDay = YearMonth.of(year, month).lengthOfMonth();
                    dd          = String.format(GS1Constants_DateTime.PAD_TWO_DIGITS, lastDay);
                }
            }

            String mm   = formatMonth(month, config.getMonthFormat());
            String yyyy = formatYear(year, config.getYearFormat());
            return assemble(config.getDateEndian(), sep, dd, mm, yyyy);

        } catch (NumberFormatException | DateTimeException e) {
            return null;
        }
    }

    /**
     * Renders the month either as a two-digit number ({@code "12"}) or a
     * three-letter abbreviation ({@code "DEC"}) per {@code monthFormat}. Falls
     * back to the two-digit form for an out-of-range month.
     */
    static String formatMonth(int month, GaiaConstants.MonthFormat monthFormat) {
        if (monthFormat == GaiaConstants.MonthFormat.THREE_LETTER
                && month >= GS1Constants_DateTime.FIRST_MONTH && month <= GS1Constants_DateTime.LAST_MONTH) {
            return GS1Constants_DateTime.MONTH_ABBREVIATIONS[month - 1];
        }
        return String.format(GS1Constants_DateTime.PAD_TWO_DIGITS, month);
    }

    /**
     * Renders the year either as four digits ({@code "2026"}) or two digits
     * ({@code "26"}) per {@code yearFormat}.
     */
    static String formatYear(int year, GaiaConstants.YearFormat yearFormat) {
        if (yearFormat == GaiaConstants.YearFormat.TWO_DIGIT) {
            return String.format(GS1Constants_DateTime.PAD_TWO_DIGITS, Math.floorMod(year, GS1Constants_DateTime.YEARS_PER_CENTURY));
        }
        return String.format(GS1Constants_DateTime.PAD_FOUR_DIGITS, year);
    }

    /** Assembles day, month, year parts in the order dictated by {@code endian}. */
    static String assemble(GaiaConstants.DateEndian endian, String sep,
                           String dd, String mm, String yyyy) {
        switch (endian) {
            case MIDDLE: return mm + sep + dd + sep + yyyy;
            case BIG:    return yyyy + sep + mm + sep + dd;
            case LITTLE:
            default:     return dd + sep + mm + sep + yyyy;
        }
    }

    /** Returns the human-readable date pattern string for the given config, e.g. {@code "dd/mm/yyyy"}. */
    static String datePattern(ParseConfig config) {
        String sep  = config.getDateSeparator().symbol();
        String mm   = config.getMonthFormat() == GaiaConstants.MonthFormat.THREE_LETTER ? GS1Constants_DateTime.TOKEN_MONTH_ABBR : GS1Constants_DateTime.TOKEN_MONTH;
        String yyyy = config.getYearFormat()  == GaiaConstants.YearFormat.TWO_DIGIT     ? GS1Constants_DateTime.TOKEN_YEAR_TWO   : GS1Constants_DateTime.TOKEN_YEAR_FOUR;
        switch (config.getDateEndian()) {
            case MIDDLE: return mm + sep + GS1Constants_DateTime.TOKEN_DAY + sep + yyyy;
            case BIG:    return yyyy + sep + mm + sep + GS1Constants_DateTime.TOKEN_DAY;
            case LITTLE:
            default:     return GS1Constants_DateTime.TOKEN_DAY + sep + mm + sep + yyyy;
        }
    }
}
