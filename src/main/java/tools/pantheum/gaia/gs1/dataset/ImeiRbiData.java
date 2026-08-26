package tools.pantheum.gaia.gs1.dataset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Reference data for IMEI Reporting Body Identifiers (RBIs) — the leading 2 digits of a
 * Type Allocation Code, naming the GSMA-appointed body that allocated it.
 *
 * <p>Three kinds of code appear in {@link #REPORTING_BODIES}:
 * <ul>
 *   <li><strong>Currently allocating</strong> — {@code 01} (CTIA/PTCRB), {@code 35}
 *       (TÜV SÜD BABT), {@code 86} (TAF), plus {@code 99} (Global Hexadecimal
 *       Administrator) and {@code 98} (reserved).</li>
 *   <li><strong>Test ranges</strong> — {@code 00} and {@code 02}–{@code 09}, which mark
 *       test IMEIs rather than a real allocation. See {@link #isTestCode(String)}.</li>
 *   <li><strong>No longer allocating</strong> — historic bodies such as {@code 49}
 *       (BZT/BAPT, Germany) or {@code 44} (BABT, UK). Handsets carrying them are ordinary
 *       and remain in service; only new allocation has ceased. See
 *       {@link #isNoLongerAllocating(String)}.</li>
 * </ul>
 *
 * <h2>Source and staleness</h2>
 * <p>Compiled from Wikipedia's Reporting Body Identifier listing, not from the GSMA
 * directly, so it may lag the authoritative allocation list (GSMA TS.06 / the IMEI
 * allocation process). Codes outside the ranges above are simply absent.
 *
 * <p>An unrecognised code therefore means <strong>"not in this table"</strong>, never
 * "invalid IMEI" — a handset may legitimately carry a prefix newer than this file.
 * Callers must treat {@link #nameForCode(String)} returning {@link Optional#empty()} as a
 * non-event and must not derive any validation outcome from it. The RBI is not a check
 * character and has no verifiable relationship to the rest of the IMEI.
 *
 * @see tools.pantheum.gaia.gs1.interpretation.enricher.ImeiEnricher
 */
public final class ImeiRbiData {

    /**
     * Maps 2-digit Reporting Body Identifiers to the allocating body's name.
     * Iteration order groups the codes as the class documentation lists them — test
     * ranges, then currently allocating, then retired — rather than sorting by code.
     * Source: Wikipedia's RBI listing — see the class documentation.
     */
    public static final Map<String, String> REPORTING_BODIES;

    /** Codes denoting a test IMEI rather than a real allocation: {@code 00}, {@code 02}–{@code 09}. */
    public static final Set<String> TEST_CODES;

    /** Codes whose bodies no longer allocate IMEIs. Existing devices remain valid. */
    public static final Set<String> NO_LONGER_ALLOCATING;

    static {
        Map<String, String> bodies = new LinkedHashMap<>();
        Set<String> tests = new LinkedHashSet<>();
        Set<String> retired = new LinkedHashSet<>();

        // Test ranges — 00 for nations with 2-digit country codes, 02–09 for 3-digit.
        bodies.put("00", "Test IMEI (2-digit country codes)");
        tests.add("00");
        for (int i = 2; i <= 9; i++) {
            String code = "0" + i;
            bodies.put(code, "Test IMEI (3-digit country codes)");
            tests.add(code);
        }

        // Currently allocating.
        bodies.put("01", "CTIA / PTCRB (United States)");
        bodies.put("35", "TÜV SÜD BABT (United Kingdom)");
        bodies.put("86", "TAF (China)");
        bodies.put("98", "Reserved for future use");
        bodies.put("99", "Global Hexadecimal Administrator (GHA)");

        // No longer allocating.
        bodies.put("10", "DECT devices");
        bodies.put("30", "Iridium (United States)");
        bodies.put("33", "DGPT (France)");
        bodies.put("44", "BABT (United Kingdom)");
        bodies.put("45", "NTA (Denmark)");
        bodies.put("49", "BZT / BAPT (Germany)");
        bodies.put("50", "BZT ETS (Germany)");
        bodies.put("51", "Cetecom ICT (Germany)");
        bodies.put("52", "Cetecom (Germany)");
        bodies.put("53", "TÜV (Germany)");
        bodies.put("54", "Phoenix Test Lab (Germany)");
        bodies.put("91", "MSAI (India)");
        Collections.addAll(retired, "10", "30", "33", "44", "45", "49",
                                    "50", "51", "52", "53", "54", "91");

        REPORTING_BODIES     = Collections.unmodifiableMap(bodies);
        TEST_CODES           = Collections.unmodifiableSet(tests);
        NO_LONGER_ALLOCATING = Collections.unmodifiableSet(retired);
    }

    private ImeiRbiData() {}

    /**
     * Returns the reporting body's name for the given 2-digit RBI, or
     * {@link Optional#empty()} if the code is not present in this table.
     *
     * @param code two-digit string, e.g. {@code "35"} for TÜV SÜD BABT
     */
    public static Optional<String> nameForCode(String code) {
        return Optional.ofNullable(REPORTING_BODIES.get(code));
    }

    /**
     * Whether the code marks a test IMEI ({@code 00}, {@code 02}–{@code 09}) rather than
     * a real allocation.
     */
    public static boolean isTestCode(String code) {
        return TEST_CODES.contains(code);
    }

    /**
     * Whether the code belongs to a body that no longer allocates IMEIs. Reporting only:
     * devices allocated under such a code remain valid and in service.
     */
    public static boolean isNoLongerAllocating(String code) {
        return NO_LONGER_ALLOCATING.contains(code);
    }
}
