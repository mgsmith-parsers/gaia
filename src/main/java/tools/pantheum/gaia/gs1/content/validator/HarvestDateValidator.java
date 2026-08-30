package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.util.DateUtils;
import tools.pantheum.gaia.gs1.util.IntUtils;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7007 — Harvest Date (YYMMDD[YYMMDD]).
 *
 * <p>The standard pipeline already verifies that each date component is a
 * valid YYMMDD date (month 01–12, day 01–31 or 00 for end-of-month) via the
 * {@code yymmdd} format validator. This validator applies the additional
 * semantic rule:
 *
 * <ul>
 *   <li>If the harvest period spans <em>one</em> calendar day, the end date
 *       <strong>must not</strong> be specified — only the start date should
 *       be present.</li>
 *   <li>If the harvest period spans <em>multiple</em> calendar days, both
 *       start and end dates must be present and the end date must be strictly
 *       greater than the start date.</li>
 * </ul>
 *
 * <h2>Component structure</h2>
 * <pre>
 *   [ Start date YYMMDD (mandatory) ][ End date YYMMDD (optional) ]
 *   Component 0                        Component 1
 * </pre>
 *
 * <p>Years are resolved using the GS1 sliding-window rule (±49/50 years
 * from today) before comparison, ensuring correct handling of dates that
 * span a century boundary.
 */
public final class HarvestDateValidator implements ContentInterface {

    /** The singleton instance. */
    public static final HarvestDateValidator INSTANCE = new HarvestDateValidator();

    /** Creates a new {@link HarvestDateValidator}. */
    public HarvestDateValidator() {}

    static final String ERR_CODE_END_DATE_SINGLE_DAY = "GE-C150";
    static final String ERR_CODE_START_AFTER_END     = "GE-C151";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();

        // Only one date — nothing to compare
        if (components.size() < 2) return List.of();

        GS1AIComponentValue startComp = components.get(0);
        GS1AIComponentValue endComp   = components.get(1);

        // End date is optional — skip if absent or empty
        if (endComp.getValue() == null || endComp.getValue().isEmpty()) return List.of();

        String startDate = startComp.getValue();
        String endDate   = endComp.getValue();

        // Both dates must be exactly 6 digits (already guaranteed by standard pipeline)
        if (startDate.length() != 6 || endDate.length() != 6) return List.of();

        int startEncoded = toComparableInt(startDate);
        int endEncoded   = toComparableInt(endDate);

        if (startEncoded < 0 || endEncoded < 0) return List.of(); // parse failure — already caught

        if (startEncoded == endEncoded) {
            // Single calendar day — end date must not be specified
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_END_DATE_SINGLE_DAY, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        if (startEncoded > endEncoded) {
            // Multi-day period with inverted dates
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_START_AFTER_END, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        // startEncoded < endEncoded — valid multi-day period
        return List.of();
    }

    // -------------------------------------------------------------------------

    /**
     * Converts a YYMMDD string to a comparable integer of the form YYYYMMDD,
     * using the GS1 sliding-window year resolution. Returns -1 on parse failure.
     */
    private static int toComparableInt(String yymmdd) {
        int yy = IntUtils.parseDigits(yymmdd, 0, 2);
        int mm = IntUtils.parseDigits(yymmdd, 2, 4);
        int dd = IntUtils.parseDigits(yymmdd, 4, 6);
        if (yy < 0 || mm < 0 || dd < 0) return -1;

        int yyyy = DateUtils.resolveYear(yy);

        // Day 00 means last day of the month — treat as end-of-month (day 31)
        // so that a start of YYMM00 compares correctly against YYMM01..YYMM31
        if (dd == 0) dd = 31;

        return yyyy * 10000 + mm * 100 + dd;
    }
}
