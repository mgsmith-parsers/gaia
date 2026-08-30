package tools.pantheum.gaia.gs1.dataset;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;
import tools.pantheum.gaia.gs1.util.ResourceLoadLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reference data for ISO 3166-1 country codes, loaded from the bundled
 * {@code iso_3166.json} resource at class-initialisation time.
 *
 * <p>Provides two maps keyed by code, with the country name as the value:
 * <ul>
 *   <li>{@link #NUMERIC_TO_NAME} — numeric code → country name (e.g. {@code "036" → "Australia"})</li>
 *   <li>{@link #ALPHA2_TO_NAME}  — alpha-2 code → country name (e.g. {@code "AU" → "Australia"})</li>
 * </ul>
 *
 * <p>Use {@link #nameForNumeric(String)} or {@link #nameForAlpha2(String)} for lookups.
 * Both maps are immutable. If the resource cannot be loaded the maps are empty and a
 * warning is printed to stderr; validators then skip the lookup and fail open.
 */
public final class Iso3166Data {

    /** Maps 3-digit numeric codes (e.g. {@code "036"}) to country names. */
    public static final Map<String, String> NUMERIC_TO_NAME;

    /** Maps alpha-2 codes (e.g. {@code "AU"}) to country names. */
    public static final Map<String, String> ALPHA2_TO_NAME;

    static {
        Map<String, String> numeric = new HashMap<>();
        Map<String, String> alpha2  = new HashMap<>();

        try (InputStream is = Iso3166Data.class.getResourceAsStream(GS1Constants_System.ISO_3166)) {
            if (is == null) {
                ResourceLoadLogger.resourceNotFound("Iso3166Data", GS1Constants_System.ISO_3166, "ISO 3166 lookup disabled");
            } else {
                JsonNode root = new ObjectMapper().readTree(is);
                for (JsonNode entry : root.path("isoCountries")) {
                    String num  = entry.path("numeric").asText(null);
                    String a2   = entry.path("alpha2").asText(null);
                    String name = entry.path("name").asText(null);
                    if (num != null && name != null) numeric.put(num, name);
                    if (a2  != null && name != null) alpha2.put(a2,  name);
                }
            }
        } catch (IOException e) {
            ResourceLoadLogger.loadFailed("Iso3166Data", GS1Constants_System.ISO_3166, e.getMessage());
        }

        NUMERIC_TO_NAME = Collections.unmodifiableMap(numeric);
        ALPHA2_TO_NAME  = Collections.unmodifiableMap(alpha2);
    }

    private Iso3166Data() {}

    /**
     * Returns the country name for the given ISO 3166-1 numeric code, or
     * {@link Optional#empty()} if the code is not recognised or the data is unavailable.
     *
     * @param numericCode zero-padded 3-digit string, e.g. {@code "036"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> nameForNumeric(String numericCode) {
        return Optional.ofNullable(NUMERIC_TO_NAME.get(numericCode));
    }

    /**
     * Returns the country name for the given ISO 3166-1 alpha-2 code, or
     * {@link Optional#empty()} if the code is not recognised or the data is unavailable.
     *
     * @param alpha2Code two-letter uppercase string, e.g. {@code "AU"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> nameForAlpha2(String alpha2Code) {
        return Optional.ofNullable(ALPHA2_TO_NAME.get(alpha2Code));
    }
}
